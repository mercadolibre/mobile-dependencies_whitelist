package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.factories.PublishTaskFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by saguilera on 7/21/17.
 */
class AndroidLibraryPublishableModule extends PublishableModule {

    private static final String PACKAGE_TYPE = 'aar'

    private Project project

    @Override
    void configure(Project project) {
        super.configure(project)

        this.project = project

        applyPlugins()
        createTasks()
    }

    @Override
    protected String packageType() {
        return PACKAGE_TYPE
    }

    private void applyPlugins() {
        project.apply plugin: 'com.github.dcendents.android-maven'

        project.afterEvaluate {
            project.rootProject.buildscript.configurations.classpath.each {
                if (it.path.contains(JACOCO_PLUGIN_CLASSPATH)) {
                    project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
                }
                if (it.path.contains(ROBOLECTRIC_PLUGIN_CLASSPATH)) {
                    project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'
                }
            }
        }
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    private void createTasks() {
        project.android.libraryVariants.all { variant ->
            // Create sources tasks if they dont exist
            if (!project.tasks.findByName("${variant.buildType.name}SourcesJar")) {
                def sourcesJarTask = project.tasks.create "${variant.buildType.name}SourcesJar", Jar
                sourcesJarTask.dependsOn variant.javaCompile
                sourcesJarTask.classifier = 'sources'
                sourcesJarTask.from variant.javaCompile.source
            }

            createAlpha(variant.buildType.name)
            createExperimental(variant.buildType.name)
            createRelease(variant.buildType.name)
            createLocal(variant.buildType.name)
        }

        createAlpha()
        createExperimental()
        createRelease()
        createLocal()
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
            it.dependencies = [ "assemble${variant?.capitalize() ?: 'Release'}", "test${variant?.capitalize() ?: 'Release'}UnitTest", "check", "${variant ?: 'release'}SourcesJar" ]
            it.project = this.project
            it.name = "alpha${variant?.capitalize() ?: ''}"
            it.variant = variant
            it.addArtifacts = artifacts
            return it
        })
    }

    private void createLocal(String variant = null) {
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = [ "assemble${variant?.capitalize() ?: 'Release'}", "${variant ?: 'release'}SourcesJar" ]
            it.project = this.project
            it.name = "local${variant?.capitalize() ?: ''}"
            it.variant = variant
            it.finalizedBy = 'uploadArchives'
            it.doFirst = { Project project ->
                project.android.defaultPublishConfig = "${variant ?: 'release'}"

                def version = project.publisher.version
                project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${variant?.toUpperCase() ?: 'RELEASE'}-${version}-${getTimestamp()}"
                project.uploadArchives.repositories.mavenDeployer.pom.artifactId = project.publisher.artifactId
                project.uploadArchives.repositories.mavenDeployer.pom.groupId = project.publisher.groupId

                def pom = project.uploadArchives.repositories.mavenDeployer.pom
                project.version = pom.version
                project.group = project.publisher.groupId

                pom.writeTo("build/poms/pom-default.xml")

                // Point the repository to our .m2/repository directory.
                project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
            }
            it.addArtifacts = { ArtifactHandler artifact, String artifactVariant ->
                artifact.add('archives', project.file(getAarFilePathForLocalPublish(artifactVariant)))
            }
            return it
        })
    }

    private void createRelease(String variant = null) {
        // Creating a release with variant will simply create that release in that variant mode.
        // Please note that the release wont have any preffix or suffix of that variant, its just the release
        PublishTaskFactory.create(new PublishTaskFactory.Builder().with {
            it.packageType = PACKAGE_TYPE
            it.dependencies = [ "assemble${variant?.capitalize() ?: 'Release'}", "test${variant?.capitalize() ?: 'Release'}UnitTest", "check", "${variant ?: 'release'}SourcesJar" ]
            it.project = this.project
            it.name = "release${variant?.capitalize() ?: ''}"
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
            it.dependencies = [ "assemble${variant?.capitalize() ?: 'Release'}", "${variant ?: 'release'}SourcesJar" ]
            it.project = this.project
            it.name = "experimental${variant?.capitalize() ?: ''}"
            it.repoName = 'android-experimental'
            it.variant = variant
            it.addArtifacts = artifacts
            return it
        })
    }

    private def artifacts = { ArtifactHandler artifacts, String variant ->
        artifacts.add('archives', getAarFile(variant))
        artifacts.add('archives', project.tasks["${variant ?: 'release'}SourcesJar"])
    }

    private def getAarFile(String variant) {
        if (variant == null) {
            variant = 'release'
        }

        def aarParentDirectory = "$project.buildDir/outputs/aar/"
        File aarFile = project.file(aarParentDirectory + "${project.name}-${variant}.aar")
        File actualFile = project.file(aarParentDirectory + "${project.publisher.artifactId}.aar")

        if (aarFile.exists() && aarFile.path != actualFile.path) {
            if (actualFile.exists()) {
                actualFile.delete()
            }
            actualFile << aarFile.bytes
        }

        return actualFile
    }

    private String getAarFilePathForLocalPublish(def variant) {
        if (variant == null) {
            variant = 'release'
        }

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
