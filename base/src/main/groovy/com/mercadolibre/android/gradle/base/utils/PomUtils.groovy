package com.mercadolibre.android.gradle.base.utils

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPom

/**
 * Created by saguilera on 7/21/17.
 */
class PomUtils {

    private static final String DEPENDENCY_LOCK_FILE_NAME = "dependencies.lock"

    static void composeLocalDependencies(Project project, XmlProvider xmlProvider) {
        // This is a "compose" where we change local dependencies for their group:artifact:version declared
        xmlProvider.asNode().dependencies.'*'.findAll() {
            it.groupId.text() == project.rootProject.name &&
                    (it.version.text() == 'unspecified' || it.version.text() == 'undefined')
        }.each {
            it.groupId*.value = project.group

            // Version might contain a timestamp, meaning that if we put for other local versions
            // this same timestamp, they wont be resolvable.
            // Check if a timestamp is present, if so, make it dynamic, else keep the version
            // This regex matches if: Starts with garbage + '-' + ends with 10 to 16 digits.
            // Eg: ALPHA-RELEASE-9.3.2-201704080505 -> ALPHA-RELEASE-9.3.2-+
            String pomVersion = xmlProvider.asNode().version*.text()
            if (pomVersion ==~ /^.*-\d{10,16}/) {
                it.version*.value = pomVersion.replaceAll(/-\d{10,16}/, '-+')
            } else {
                it.version*.value = pomVersion
            }

            // Check over all the subprojects for someone with that project name and use its name
            def artifact = project.rootProject.subprojects.find { subproject ->
                subproject.name == it.artifactId.text()
            }.name

            it.artifactId*.value = artifact

            // In local dependencies a classifier is added for knowing the compiled variant, remove it.
            it.remove(it.classifier)
        }
    }

    static void composeDynamicDependencies(Project project, XmlProvider xmlProvider) {
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

    static class Builder {

        Project project = null
        String packageType = null

    }

}
