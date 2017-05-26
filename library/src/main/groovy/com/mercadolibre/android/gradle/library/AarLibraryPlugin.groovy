package com.mercadolibre.android.gradle.library

import org.gradle.api.GradleException
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.GradleVersion

/**
 * Gradle plugin for Android Libraries. It provides some important tasks:
 *
 * <ol>
 *     <li>Apply com.android.library plugin.</li>
 *     <li>Apply maven plugin.</li>
 *     <li>Generate JAR with sources.</li>
 *     <li>Create lint reports.</li>
 *     <li>Run Android connected tests (requires a connected device).</li>
 *     <li>Upload the artifacts to the repository (either release, experimental or local).</li>
 *     <li>Tag the version in Git (if release).</li>
 * </ol>
 *
 * @author Martin A. Heras & Nicolas Giagnoni
 */
public class AarLibraryPlugin extends LibraryPlugin {

    private static final String TASK_PUBLISH_LOCAL = "publishAarLocal"
    private static final String TASK_PUBLISH_EXPERIMENTAL = "publishAarExperimental"
    private static final String TASK_PUBLISH_RELEASE = "publishAarRelease"
    private static final String TASK_PUBLISH_ALPHA = "publishAarAlpha"

    @Override
    String getPomPackaging() {
        return "aar"
    }

    @Override
    String getFilePathForLocalPublish() {
        return null
    }

    @Override
    boolean isPublishTask(String task) {
        return task.contains(TASK_PUBLISH_LOCAL) || task == TASK_PUBLISH_EXPERIMENTAL || task == TASK_PUBLISH_ALPHA || task == TASK_PUBLISH_RELEASE
    }

    @Override
    void applyPlugins() {
        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

        // We apply android plugin.
        project.apply plugin: 'com.android.library'

        project.apply plugin: 'com.jfrog.bintray'

        project.apply plugin: 'com.github.dcendents.android-maven'

        project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
        project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'
    }

    /**
     * Creates the tasks to generate the JARs with the source code, one per variant.
     */
    @Override
    void createSourcesJarTasks() {
        project.android.libraryVariants.all { variant ->
            def sourcesJarTask = project.tasks.create "${variant.buildType.name}SourcesJar", Jar
            sourcesJarTask.dependsOn variant.javaCompile
            sourcesJarTask.classifier = 'sources'
            sourcesJarTask.from variant.javaCompile.source
        }
    }

    /**
     * Sets up the "uploadArchives" task from the "maven" plugin.
     */
    @Override
    protected void setupUploadArchivesTask() {
        super.setupUploadArchivesTask()

        project.uploadArchives.dependsOn 'connectedAndroidTest'
    }

    /**
     * Create and configure a bintray task for a specific
     * publishType. Take into account that this type of task
     * must be a Bintray type (not a local publish for example)
     */
    @Override
    void createBintrayTask(String publishType) {
        def task;
        switch (publishType) {
            case PUBLISH_RELEASE:
                task = project.tasks.create TASK_PUBLISH_RELEASE
                task.setDescription('Publishes a new release version of the AAR library to Bintray.')
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'testReleaseUnitTest', 'check', 'releaseSourcesJar'
                break

            case PUBLISH_EXPERIMENTAL:
                task = project.tasks.create TASK_PUBLISH_EXPERIMENTAL
                task.setDescription('Publishes a new experimental version of the AAR library.')
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'releaseSourcesJar'
                break

            case PUBLISH_ALPHA:
                task = project.tasks.create TASK_PUBLISH_ALPHA
                task.setDescription('Publishes a new alpha version of the AAR library to Bintray.')
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'testReleaseUnitTest', 'check', 'releaseSourcesJar'
                break

            default:
                throw new GradleException("No task type provided")
        }
        task.finalizedBy 'bintrayUpload'
        task.doLast {
            setBintrayConfig(publishType);

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file(getAarFilePath()))
            project.artifacts.add('archives', project.tasks['releaseSourcesJar'])

            logVersion(String.format("%s:%s:%s", project.group, project.name, project.version))
        }
    }

    /**
     * Creates the "publishAarLocalX" task, where X are each of the library variants
     */
    @Override
    void createPublishLocalTasks() {
        project.afterEvaluate {
            project.android.libraryVariants.each { variant ->
                def flavorName = variant.buildType.name

                def task = project.tasks.create "${TASK_PUBLISH_LOCAL}${flavorName.capitalize()}"
                task.setDescription("Publishes a new local version, on the variant ${flavorName.capitalize()} of the AAR library, locally on the .m2/repository directory.")
                task.dependsOn 'checkLocalDependencies', "assemble${flavorName.capitalize()}", "${flavorName}SourcesJar"
                task.finalizedBy 'uploadArchives'

                task.doFirst {
                    project.android.defaultPublishConfig = "${flavorName}"
                }

                task.doLast {
                    // Set the artifacts.
                    if (GradleVersion.current() < GradleVersion.version('3.0')) {
                        project.configurations.default.artifacts.clear()
                    }

                    project.configurations.archives.artifacts.clear()
                    project.artifacts.add('archives', project.file(getAarFilePathForLocalPublish(flavorName)))
                    project.artifacts.add('archives', project.tasks["${flavorName}SourcesJar"])

                    def version = project.uploadArchives.repositories.mavenDeployer.pom.version
                    project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${flavorName.toUpperCase()}-${version}-${getTimestamp()}"

                    def pom = project.uploadArchives.repositories.mavenDeployer.pom
                    logVersion(String.format("%s:%s:%s", pom.groupId, pom.artifactId, pom.version))

                    // Point the repository to our .m2/repository directory.
                    project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
                }
            }
        }
    }

    /**
     * Retrieves the aar file to publish and renames it so that the bintray plugin uploads it correctly.
     * @return new file path
     */
    private String getAarFilePath() {

        def aarParentDirectory = "$project.buildDir/outputs/aar/"

        // Check if previous publish AAR exists (and delete it).
        def prevFile = project.file(aarParentDirectory + "${project.name}.aar");
        if (prevFile.exists()) {
            prevFile.delete();
        }

        // Get the AAR file and rename it (so that the bintray plugin uploads the aar to the correct path).
        File aarFile = project.file(aarParentDirectory + "${project.name}-release.aar")

        def newName = aarParentDirectory + "${getPublisherContainer().artifactId}.aar"

        aarFile.renameTo(newName)

        return newName
    }

    private String getAarFilePathForLocalPublish(def variant) {
        def aarParentDirectory = "$project.buildDir/outputs/aar/"

        // Check if previous publish AAR exists (and delete it).
        def prevFile = project.file(aarParentDirectory + "${project.name}.aar");
        if (prevFile.exists()) {
            prevFile.delete();
        }

        // Get the AAR file and rename it (so that the bintray plugin uploads the aar to the correct path).
        File aarFile = project.file(aarParentDirectory + "${project.name}-${variant}.aar")

        def newName = aarParentDirectory + "${project.name}-release.aar"

        aarFile.renameTo(newName)

        return newName
    }
}