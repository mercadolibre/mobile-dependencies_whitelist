package com.mercadolibre.android.gradle.library

import org.gradle.api.GradleException
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.GradleVersion

/**
 * Created by mfeldsztejn on 5/23/17.
 */
public class JarLibraryPlugin extends LibraryPlugin {

    private static final String TASK_PUBLISH_LOCAL = "publishJarLocal"
    private static final String TASK_PUBLISH_EXPERIMENTAL = "publishJarExperimental"
    private static final String TASK_PUBLISH_RELEASE = "publishJarRelease"
    private static final String TASK_PUBLISH_ALPHA = "publishJarAlpha"

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void applyPlugins() {

        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

        project.apply plugin: 'java'

        project.apply plugin: 'com.jfrog.bintray'
    }

    @Override
    boolean isPublishTask(String task) {
        return task == TASK_PUBLISH_LOCAL || task == TASK_PUBLISH_EXPERIMENTAL || task == TASK_PUBLISH_ALPHA || task == TASK_PUBLISH_RELEASE
    }

    /**
     * Creates the tasks to generate the JARs with the source code
     */
    void createSourcesJarTasks() {
        def sourcesJarTask = project.tasks.create "sourcesJar", Jar
        sourcesJarTask.dependsOn project.tasks.getByName("compileJava")
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.from project.tasks.getByName("compileJava").source
    }

    /**
     * Create and configure a bintray task for a specific
     * publishType. Take into account that this type of task
     * must be a Bintray type (not a local publish for example)
     */
    void createBintrayTask(String publishType) {
        def task;
        switch (publishType) {
            case PUBLISH_RELEASE:
                task = project.tasks.create TASK_PUBLISH_RELEASE
                task.setDescription('Publishes a new release version of the JAR library to Bintray.')
                task.dependsOn 'checkLocalDependencies', 'assemble', 'test', 'check', 'sourcesJar', 'build'
                break

            case PUBLISH_EXPERIMENTAL:
                task = project.tasks.create TASK_PUBLISH_EXPERIMENTAL
                task.setDescription('Publishes a new experimental version of the JAR library.')
                task.dependsOn 'checkLocalDependencies', 'assemble', 'test', 'check', 'sourcesJar', 'build'
                break

            case PUBLISH_ALPHA:
                task = project.tasks.create TASK_PUBLISH_ALPHA
                task.setDescription('Publishes a new alpha version of the JAR library to Bintray.')
                task.dependsOn 'checkLocalDependencies', 'assemble', 'test', 'check', 'sourcesJar', 'build'
                break

            default:
                throw new GradleException("No task type provided")
        }
        task.finalizedBy 'bintrayUpload'
        task.doLast {
            setBintrayConfig(publishType);

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file(getFilePath()))
            project.artifacts.add('archives', project.tasks['sourcesJar'])

            logVersion(String.format("%s:%s:%s", project.group, project.name, project.version))
        }
    }

    /**
     * Retrieves the aar file to publish and renames it so that the bintray plugin uploads it correctly.
     * @return new file path
     */
    private String getFilePath() {
        def jarParentDirectory = "$project.buildDir/libs/"
        def actualDestination = jarParentDirectory + "${publisherContainer.artifactId}.jar"

        // Check if previous publish AAR exists (and delete it).
        def prevFile = project.file(jarParentDirectory + "${project.name}.jar");
        if (prevFile.exists()) {
            prevFile.renameTo(actualDestination)
        }

        return actualDestination
    }

    String getFilePathForLocalPublish() {
        return "$project.buildDir/libs/${project.name}.jar";
    }

    /**
     * Creates the "publishAarLocalX" task, where X are each of the library variants
     */
    void createPublishLocalTasks() {
        project.afterEvaluate {
            def task = project.tasks.create "${TASK_PUBLISH_LOCAL}"
            task.setDescription("Publishes a new local version, on the variant of the AAR library, locally on the .m2/repository directory.")
            task.dependsOn 'checkLocalDependencies', "assemble"
            task.finalizedBy 'uploadArchives'

            task.doLast {
                // Set the artifacts.
                if (GradleVersion.current() < GradleVersion.version('3.0')) {
                    project.configurations.default.artifacts.clear()
                }

                project.artifacts.add('archives', project.file(filePathForLocalPublish))

                def version = project.uploadArchives.repositories.mavenDeployer.pom.version
                project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${version}-${getTimestamp()}"

                def pom = project.uploadArchives.repositories.mavenDeployer.pom
                logVersion(String.format("%s:%s:%s", pom.groupId, pom.artifactId, pom.version))

                // Point the repository to our .m2/repository directory.
                project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
            }
        }
    }

    String getPomPackaging() {
        return "jar"
    }

}
