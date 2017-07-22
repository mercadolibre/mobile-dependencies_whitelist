package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.factories.PublishTaskFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by saguilera on 7/21/17.
 */
class JavaPublishableModule extends PublishableModule {

    private static final String PACKAGE_TYPE = 'jar'

    private Project project

    @Override
    void configure(Project project) {
        super.configure(project)

        this.project = project

        applyPlugins()
        createTasks()
    }

    private void applyPlugins() {
        project.afterEvaluate {
            project.rootProject.buildscript.configurations.classpath.each {
                if (it.path.contains(JACOCO_PLUGIN_CLASSPATH)) {
                    project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
                }
            }
        }
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    private void createTasks() {
        def sourcesJarTask = project.tasks.create "releaseSourcesJar", Jar
        sourcesJarTask.dependsOn project.tasks.getByName("compileJava")
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.from project.tasks.getByName("compileJava").source

        createAlpha()
        createExperimental()
        createRelease()
        createLocal()
    }

    private taskDependencies = {
        return ["checkLocalDependencies", "assemble", "test", "check", "releaseSourcesJar"]
    }

    private def artifacts = { ArtifactHandler artifacts, String variant ->
        artifacts.add('archives', getJarFile())
        artifacts.add('archives', project.tasks["releaseSourcesJar"])
    }

    private void createAlpha() {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.suffixVersion = "ALPHA-${getTimestamp()}"
            it.dependencies = taskDependencies()
            it.project = project
            it.name = "alpha"
            it.addArtifacts = artifacts
            return it
        })
    }

    private void createLocal() {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = taskDependencies()
            it.project = project
            it.name = "local"
            it.finalizedBy = 'uploadArchives'
            it.doLast = { Project project ->
                project.artifacts.add('archives', project.file("$project.buildDir/libs/${project.name}.jar"))

                def version = project.uploadArchives.repositories.mavenDeployer.pom.version
                project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${version}-${getTimestamp()}"

                def pom = project.uploadArchives.repositories.mavenDeployer.pom
                project.version = pom.version
                project.group = pom.groupId
                project.publisher.artifactId = pom.artifactId

                // Point the repository to our .m2/repository directory.
                project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
            }
            return it
        })
    }

    private void createRelease() {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = taskDependencies()
            it.project = project
            it.name = "release"
            it.addArtifacts = artifacts
            return it
        })
    }

    private void createExperimental() {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.suffixVersion = getTimestamp()
            it.prefixVersion = "EXPERIMENTAL"
            it.dependencies = taskDependencies()
            it.project = project
            it.name = "experimental"
            it.repoName = 'android-experimental'
            it.addArtifacts = artifacts
            return it
        })
    }

    private def getJarFile() {
        def jarParentDirectory = "$project.buildDir/libs/"
        def prevFile = project.file(jarParentDirectory + "${project.name}.jar");
        def actualFile = project.file(jarParentDirectory + "${project.publisher.artifactId}-${project.publisher.version}.jar")

        if (prevFile.exists() && prevFile.path != actualFile.path) {
            if (actualFile.exists()) {
                actualFile.delete()
            }
            actualFile << prevFile.bytes
        }

        return actualFile
    }

}
