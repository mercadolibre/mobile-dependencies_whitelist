package com.mercadolibre.android.gradle.library.modules

import com.mercadolibre.android.gradle.library.factories.PublishTaskFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by saguilera on 7/21/17.
 */
class JavaModule extends Module {

    private static final String PACKAGE_TYPE = 'jar'

    private Project project

    @Override
    void configure(Project project) {
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
        project.sourceSets.all { def type ->
            // Create sources tasks
            def sourcesJarTask = project.tasks.create "${type.name}SourcesJar", Jar
            sourcesJarTask.from type.output
            sourcesJarTask.classifier = "${type.name}"

            createAlpha(type.name)
            createExperimental(type.name)
            createRelease(type.name)
            createLocal(type.name)
        }

        if (!project.tasks.findByName('releaseSourcesJar')) {
            def sourcesJarTask = project.tasks.create "releaseSourcesJar", Jar
            sourcesJarTask.dependsOn project.tasks.getByName("compileJava")
            sourcesJarTask.classifier = 'sources'
            sourcesJarTask.from project.tasks.getByName("compileJava").source
        }

        createAlpha()
        createExperimental()
        createRelease()
        createLocal()
    }

    private taskDependencies = { String variant = null ->
        return ["checkLocalDependencies", "assemble${variant?.capitalize() ?: ''}", "test${variant?.capitalize() ?: ''}", "check", "${variant?.capitalize() ?: 'release'}SourcesJar"]
    }

    private def artifacts = { ArtifactHandler artifacts, String variant ->
        artifacts.add('archives', getJarFile())
        artifacts.add('archives', project.tasks["${variant ?: 'release'}SourcesJar"])
    }

    private void createAlpha(String variant = null) {
        String defaultSuffixVersion = "ALPHA-${getTimestamp()}"
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            if (variant) {
                it.suffixVersion = "${variant.toUpperCase()}-${defaultSuffixVersion}"
            } else {
                it.suffixVersion = defaultSuffixVersion
            }
            it.dependencies = taskDependencies(variant)
            it.project = project
            it.name = "alpha${variant.capitalize() ?: ''}"
            it.variant = variant
            it.addArtifacts = artifacts
            return it
        })
    }

    private void createLocal(String variant = null) {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = taskDependencies(variant)
            it.project = project
            it.name = "local${variant.capitalize() ?: ''}"
            it.variant = variant
            it.finalizedBy = 'uploadArchives'
            it.doFirst = { Project project ->
                project.android.defaultPublishConfig = "${variant}"
            }
            it.doLast = { Project project ->
                project.artifacts.add('archives', project.file("$project.buildDir/libs/${project.name}.jar"))

                def version = project.uploadArchives.repositories.mavenDeployer.pom.version
                project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${variant.toUpperCase()}-${version}-${getTimestamp()}"

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

    private void createRelease(String variant = null) {
        // Creating a release with variant will simply create that release in that variant mode.
        // Please note that the release wont have any preffix or suffix of that variant, its just the release
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = taskDependencies(variant)
            it.project = project
            it.name = "release${variant.capitalize() ?: ''}"
            it.variant = variant
            it.addArtifacts = artifacts
            return it
        })
    }

    private void createExperimental(String variant = null) {
        String defaultPrefixVersion = "EXPERIMENTAL"
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.suffixVersion = getTimestamp()
            if (variant) {
                it.prefixVersion = "${defaultPrefixVersion}-${variant.toUpperCase()}"
            } else {
                it.prefixVersion = defaultPrefixVersion
            }
            it.dependencies = taskDependencies(variant)
            it.project = project
            it.name = "experimental${variant?.capitalize() ?: ''}"
            it.repoName = 'android-experimental'
            it.variant = variant
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
