package com.mercadolibre.android.gradle.library

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

    private static final String TASK_PUBLISH_AAR_LOCAL = "publishAarLocal"
    private static final String TASK_PUBLISH_AAR_EXPERIMENTAL = "publishAarExperimental"
    private static final String TASK_PUBLISH_AAR_RELEASE = "publishAarRelease"
    private static final String TASK_PUBLISH_AAR_ALPHA = "publishAarAlpha"
    private static final String JACOCO_PLUGIN_CLASSPATH = "com.mercadolibre.android.gradle/jacoco"
    private static final String ROBOLECTRIC_PLUGIN_CLASSPATH = "com.mercadolibre.android.gradle/robolectric"

    @Override
    String getPomPackaging() {
        return "aar"
    }

    @Override
    boolean isPublishTask(String task) {
        return super.isPublishTask(task) || task.contains(TASK_PUBLISH_AAR_LOCAL) || task == TASK_PUBLISH_AAR_EXPERIMENTAL ||
                task == TASK_PUBLISH_AAR_ALPHA || task == TASK_PUBLISH_AAR_RELEASE
    }

    @Override
    void applyPlugins() {
        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

        // We apply android plugin.
        project.apply plugin: 'com.android.library'

        project.apply plugin: 'com.jfrog.bintray'

        project.apply plugin: 'com.github.dcendents.android-maven'

        project.afterEvaluate {
            boolean hasJacocoPlugin, hasRobolectricPlugin
            project.rootProject.buildscript.configurations.classpath.each {
                if (it.name.contains(JACOCO_PLUGIN_CLASSPATH)) {
                    hasJacocoPlugin = true
                }
                if (it.name.contains(ROBOLECTRIC_PLUGIN_CLASSPATH)) {
                    hasRobolectricPlugin = true
                }
            }
            if (hasJacocoPlugin)
                project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'

            if (hasRobolectricPlugin)
                project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'
        }
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

    @Override
    void setTaskDependencies(def task, def publishType) {
        switch (publishType) {
            case PUBLISH_RELEASE:
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'testReleaseUnitTest', 'check', 'releaseSourcesJar'
                break
            case PUBLISH_EXPERIMENTAL:
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'releaseSourcesJar'
                break
            case PUBLISH_ALPHA:
                task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'testReleaseUnitTest', 'check', 'releaseSourcesJar'
                break
        }
    }

    @Override
    void createBintrayTask(String publishType) {
        super.createBintrayTask(publishType)
        def task
        switch (publishType) {
            case PUBLISH_RELEASE:
                task = project.tasks.create "${TASK_PUBLISH_AAR_RELEASE}"
                task.finalizedBy TASK_PUBLISH_RELEASE
                break

            case PUBLISH_EXPERIMENTAL:
                task = project.tasks.create "${TASK_PUBLISH_AAR_EXPERIMENTAL}"
                task.finalizedBy TASK_PUBLISH_EXPERIMENTAL
                break

            case PUBLISH_ALPHA:
                task = project.tasks.create "${TASK_PUBLISH_AAR_ALPHA}"
                task.finalizedBy TASK_PUBLISH_ALPHA
                break
        }
    }

    @Override
    void addArtifacts() {
        project.artifacts.add('archives', project.file(getAarFilePath()))
        project.artifacts.add('archives', project.tasks['releaseSourcesJar'])
    }

    /**
     * Creates the "publishAarLocalX" task, where X are each of the library variants
     */
    @Override
    void createPublishLocalTasks() {
        project.afterEvaluate {
            project.android.libraryVariants.each { variant ->
                def flavorName = variant.buildType.name

                def task = project.tasks.create "${TASK_PUBLISH_AAR_LOCAL}${flavorName.capitalize()}"
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
