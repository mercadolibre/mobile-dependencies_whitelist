package com.mercadolibre.android.gradle.library.factories

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler

/**
 * Created by saguilera on 7/21/17.
 */
class PublishTaskFactory {

    private static final String BINTRAY_USER_ENV = "BINTRAY_USER"
    private static final String BINTRAY_KEY_ENV = "BINTRAY_KEY"
    private static final String BINTRAY_PROP_FILE = "bintray.properties"
    private static final String BINTRAY_USER_PROP = "bintray.user"
    private static final String BINTRAY_KEY_PROP = "bintray.key"

    // Since we dont know the repo url, we use the organization
    private static final String REPO_URL = "http://github.com/mercadolibre"

    static void create(Builder builder) {
        if (builder.project == null || builder.name == null || builder.packageType == null) {
            throw new IllegalStateException("Need a project and a publish name")
        }
        def project = builder.project

        def task = project.tasks.create "publish${builder.packageType.capitalize()}${builder.name.capitalize()}"
        task.setDescription(builder.description)
        task.dependsOn builder.dependencies
        task.finalizedBy builder.finalizedBy
        task.doFirst {
            builder.doFirst(project)
        }
        task.doLast {
            if (builder.finalizedBy == 'bintrayUpload') {
                setBintrayConfig(builder);
            }

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            builder.addArtifacts(project.artifacts, builder.variant)

            builder.doLast(project)

            logVersion(String.format("%s:%s:%s", project.group, project.name, project.version))
        }
    }

    private static def setBintrayConfig(Builder builder) {
        def project = builder.project
        def publisherContainer = project.publisher

        project.group = publisherContainer.groupId

        project.version = "${builder.prefixVersion ? builder.prefixVersion + '-' : ''}" +
                "${publisherContainer.version}" +
                "${builder.suffixVersion ? '-' + builder.suffixVersion : ''}"

        publisherContainer.version = project.version

        project.bintrayUpload.with {
            repoName = builder.repoName

            if (repoName == 'android-releases') {
                packageVcsUrl =
                        "$REPO_URL/releases/tag/v${project.version}"
                versionVcsTag = "v${project.version}"
            }

            dryRun = false
            publish = true
            configurations = ['archives']
            userOrg = 'mercadolibre'
            packageName = "${publisherContainer.groupId}.${publisherContainer.artifactId}"
            packageWebsiteUrl = REPO_URL
            versionName = "${project.version}"
            packagePublicDownloadNumbers = false

            return it
        }

        loadBintrayCredentials(project)

        // Write the correct pom for the version and artifactId being generated
        PomFactory.create(new PomFactory.Builder().with {
            it.project = builder.project
            it.repoUrl = builder.repoName
            it.packageType = builder.packageType
            return it
        }).writeTo("build/poms/pom-default.xml")

        // Rename the sources file to find it.
        project.file("$project.buildDir/libs/${project.name}-sources.jar")
                .renameTo("$project.buildDir/libs/${publisherContainer.artifactId}-${project.version}-sources.jar")
    }

    private static def loadBintrayCredentials(Project project) {
        File propsFile = project.file(BINTRAY_PROP_FILE);
        if (propsFile.exists()) {
            println "[!] Load Bintray credentials from '${BINTRAY_PROP_FILE}'. Please don't versioning this file."
            Properties props = new Properties();
            props.load(new FileInputStream(propsFile))
            project.bintrayUpload.user = props.getProperty(BINTRAY_USER_PROP)
            project.bintrayUpload.apiKey = props.getProperty(BINTRAY_KEY_PROP)
        } else if (System.getenv(BINTRAY_USER_ENV) && System.getenv(BINTRAY_KEY_ENV)) {
            project.bintrayUpload.user = System.getenv(BINTRAY_USER_ENV)
            project.bintrayUpload.apiKey = System.getenv(BINTRAY_KEY_ENV)
        } else {
            println "[!] Missing Bintray credentials"
            println "    You can set this values as enviroment variables: '${BINTRAY_USER_ENV}' and '${BINTRAY_KEY_ENV}'"
            println "    or into a property file ('${BINTRAY_PROP_FILE}'): '${BINTRAY_USER_PROP}' and '${BINTRAY_KEY_PROP}'"
        }
    }

    /**
     * Log the given {@code library}.
     * @param library It should be the string concatenation between {@code groupId}, {@code modules} and {@code version}
     */
    private static void logVersion(String library) {
        println 'Publishing library: ' + library
    }


    static class Builder {

        Project project = null

        String name = null
        String packageType = null

        String[] dependencies = []

        String prefixVersion = ''
        String suffixVersion = ''

        String variant = null

        Closure doLast = { Project -> }
        Closure doFirst = { Project -> }
        Closure addArtifacts = { ArtifactHandler artifact, String variant -> }

        String finalizedBy = 'bintrayUpload';

        String description = "Publishes a new $name version of the library."

        String repoName = 'android-releases'

    }

}
