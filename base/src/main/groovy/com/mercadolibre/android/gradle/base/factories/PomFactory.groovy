package com.mercadolibre.android.gradle.base.factories

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.maven.MavenPom

/**
 * Created by saguilera on 7/21/17.
 */
class PomFactory {

    private static final String DEPENDENCY_LOCK_FILE_NAME = "dependencies.lock"

    static MavenPom create(Builder builder) {
        Project project = builder.project
        String packagingType = builder.packageType
        String repoUrl = builder.repoUrl

        def publisher = project.publisher
        return project.pom { MavenPom pom ->
            pom.version = publisher.version
            pom.artifactId = publisher.artifactId
            pom.groupId = publisher.groupId
            pom.packaging = packagingType

            pom.project { // This is why the variable is called proj. Else they will conflict
                packaging packagingType
                url repoUrl
            }
        }.withXml { XmlProvider xmlProvider ->
            // This is a "compose" where we change local dependencies for their group:artifact:version declared
            xmlProvider.asNode().dependencies.'*'.findAll() {
                it.groupId.text() == project.rootProject.name &&
                (it.version.text() == 'unspecified' || it.version.text() == 'undefined')
            }.each {
                it.groupId*.value = publisher.groupId
                it.version*.value = publisher.version

                // Check over all the subprojects for someone with that project name and use its artifactId of publisher
                def artifact = project.rootProject.subprojects.find {
                    it.name == it.artifactId.text()
                }.publisher.artifactId

                it.artifactId*.value = artifact
            }
        }.withXml { XmlProvider xmlProvider ->
            // This is another compose where we change dynamic versions by the ones declared in the .lock (if existent)
            // If the dependency lock file exists then change + for locked.
            // Else its just a dynamic dependency declared by someone who wants it like that
            if (project.file(DEPENDENCY_LOCK_FILE_NAME).exists()) {
                def json = new JsonSlurper().parse(project.file(DEPENDENCY_LOCK_FILE_NAME))
                //For now they are all release, so check in release and compile. If in a future
                //we have also for debug, change here to find the release or debug accordingly
                xmlProvider.asNode().dependencies.'*'.findAll() {
                    it.version.text().contains('+')
                }.each { def dependency ->
                    // Look in the json if the dependency name exists
                    json.each { def config ->
                        def jsonDependency = config["${dependency.groupId.text()}:${dependency.artifactId.text()}"]
                        if (jsonDependency && jsonDependency.locked && !jsonDependency.locked.contains('+')) {
                            dependency.version*.value = jsonDependency.locked
                        }
                    }
                }
            }
        }
    }

    static class Builder {

        Project project = null
        String packageType = null
        String repoUrl = ''

    }

}
