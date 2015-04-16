package com.mercadolibre.android.gradle.library

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

import java.text.SimpleDateFormat

/**
 * Gradle plugin for Android Libraries. It provides some important tasks:
 *
 * <ol>
 *     <li>Apply com.android.library plugin.</li>
 *     <li>Apply maven plugin.</li>
 *     <li>Generate JAR with sources.</li>
 *     <li>Generate HTMLs with Javadoc.</li>
 *     <li>Generate JAR with Javadoc.</li>
 *     <li>Create lint reports.</li>
 *     <li>Run Android connected tests (requires a connected device).</li>
 *     <li>Upload the artifacts to the repository (either release, experimental or local).</li>
 *     <li>Tag the version in Git (if release).</li>
 * </ol>
 *
 * @author Martin A. Heras & Nicolas Giagnoni
 */
public class LibraryPlugin implements Plugin<Project> {

    /**
     * The project.
     */
    private Project project;

    private static final String PUBLISH_RELESE = "release"
    private static final String PUBLISH_EXPERIMENTAL = "experimental"

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {
        this.project = project

        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

        // We apply android plugin.
        project.apply plugin: 'com.android.library'

        // Apply bintray plugins to the repositories.
        project.apply plugin: 'com.jfrog.bintray'
        project.apply plugin: 'com.github.dcendents.android-maven'

        project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
        project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'

        addPublisherContainer()
        setupUploadArchivesTask()
        createAllTasks()
    }

    /**
     * Creates the "checkLocalDependencies" task.
     */
    private void createCheckLocalDependenciesTask() {
        def task = project.tasks.create 'checkLocalDependencies'
        task.setDescription('Checks that there is no declared local dependency in the build script, as this way of declaring dependencies is invalid when publishing artifacts.')
        task.doLast {
            def localDependencyFound = false;
            project.configurations.each { conf ->
                conf.allDependencies.each { dep ->
                    if ("unspecified".equalsIgnoreCase(dep.version)) {
                        localDependencyFound = true;
                    }
                }
            }
            if (localDependencyFound) {
                throw new GradleException("A local dependency is declared in '${project.name}'. Make sure that you are not declaring a dependency like \"compile project('anotherProject')\", as it is invalid for published artifacts.")
            }
        }
    }

    /**
     * Gets the 'publisher' container.
     * @return the 'publisher' container.
     */
    private PublisherPluginExtension getPublisherContainer() {
        return project.publisher
    }

    /**
     * Adds the 'publisher' container to the project.
     */
    private void addPublisherContainer() {
        project.extensions.create('publisher', PublisherPluginExtension)
    }

    /**
     * Creates all the relevant tasks for the plugin.
     */
    private void createAllTasks() {
        createSourcesJarTasks()
        createJavadocTasks()
        createJavadocJarTasks()
        createPublishLocalTask()
        createPublishReleaseTask()
        createPublishExperimentalTask()
        // TODO: Uncomment this when Git tagging needs to be used again.
        // createTagVersionTask()
        createCheckLocalDependenciesTask()
        resetUploadArchivesDependencies()
    }

    /**
     * Creates the tasks to generate the JARs with the source code, one per variant.
     */
    private void createSourcesJarTasks() {
        project.android.libraryVariants.all { variant ->
            def sourcesJarTask = project.tasks.create "${variant.buildType.name}SourcesJar", Jar
            sourcesJarTask.dependsOn variant.javaCompile
            sourcesJarTask.classifier = 'sources'
            sourcesJarTask.from variant.javaCompile.source
        }
    }

    /**
     * Creates the tasks to generate the Javadoc HTMLs, one per variant.
     */
    private void createJavadocTasks() {
        project.android.libraryVariants.all { variant ->
            def javadocTask = project.tasks.create "${variant.buildType.name}Javadoc", Javadoc
            javadocTask.source = variant.javaCompile.source
            javadocTask.classpath = project.files(variant.javaCompile.classpath.files) + project.files("${project.android.sdkDirectory}/platforms/${project.android.compileSdkVersion}/android.jar")
            javadocTask.options.links 'http://docs.oracle.com/javase/7/docs/api/'
            javadocTask.options.linksOffline 'http://d.android.com/reference/', "${project.android.sdkDirectory}/docs/reference"
            javadocTask.exclude '**/BuildConfig.java'
            javadocTask.exclude '**/R.java'
            javadocTask.failOnError = false
        }
    }

    /**
     * Creates the tasks to generate the JARs with the Javadoc HTMLs, one per variant.
     */
    private void createJavadocJarTasks() {
        project.android.libraryVariants.all { variant ->
            def javadocTask = project.tasks.findByName("${variant.buildType.name}Javadoc")
            def javadocJarTask = project.tasks.create "${variant.buildType.name}JavadocJar", Jar
            javadocJarTask.classifier = 'javadoc'
            javadocJarTask.from javadocTask.destinationDir
            javadocJarTask.dependsOn javadocTask
        }
    }

    /**
     * Sets up the "uploadArchives" task from the "maven" plugin.
     */
    private void setupUploadArchivesTask() {

        project.afterEvaluate {

            validatePublisherContainer()

            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: "file://${System.properties['user.home']}/.m2/repository")
                        pom.groupId = getPublisherContainer().groupId
                        pom.artifactId = getPublisherContainer().artifactId
                        pom.version = getPublisherContainer().version
                    }
                }
            }
        }

        project.uploadArchives.dependsOn 'connectedAndroidTest'
    }

    /**
     * Validates that all the needed configuration is set within the 'publisher' container.
     */
    private void validatePublisherContainer() {

        // Publisher container.
        if (getPublisherContainer().groupId == null || getPublisherContainer().groupId.length() == 0) {
            throw new GradleException("Property 'publisher.groupId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().artifactId == null || getPublisherContainer().artifactId.length() == 0) {
            throw new GradleException("Property 'publisher.artifactId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().version == null || getPublisherContainer().version.length() == 0) {
            throw new GradleException("Property 'publisher.version' is needed by the Publisher plugin. Please define it in the build script.")
        }

    }

    /**
     * Resets the "uploadArchives" task dependencies so that we can configure its
     * artifacts in a custom way, depending on the variants.
     */
    private void resetUploadArchivesDependencies() {
        project.tasks['uploadArchives'].dependsOn.clear()
    }

    /**
     * Creates the "publishAarRelease" task.
     */
    private void createPublishReleaseTask() {
        def task = project.tasks.create 'publishAarRelease'
        task.setDescription('Publishes a new release version of the AAR library to Bintray.')
        task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'testRelease', 'check', 'releaseSourcesJar' //, 'releaseJavadocJar' --> // Uncomment to upload Javadocs. This is not working well so it is turned off.
        task.finalizedBy 'bintrayUpload'
        task.doLast {

            // Set all Bintray configuration.
            setBintrayConfig(PUBLISH_RELESE);

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file(getAarFilePath(PUBLISH_RELESE)))
            project.artifacts.add('archives', project.tasks['releaseSourcesJar'])



        }
    }

    /**
     * Creates the "publishAarExperimental" task.
     */
    private void createPublishExperimentalTask() {
        def task = project.tasks.create 'publishAarExperimental'
        task.setDescription('Publishes a new experimental version of the AAR library.')
        task.dependsOn 'checkLocalDependencies', 'assembleDebug', 'debugSourcesJar' //, 'debugJavadocJar' --> // Uncomment to upload Javadocs. This is not working well so it is turned off.
        task.finalizedBy 'bintrayUpload'

        task.doLast {

            // Set all Bintray configuration.
            setBintrayConfig(PUBLISH_EXPERIMENTAL);

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file(getAarFilePath(PUBLISH_EXPERIMENTAL)))
            project.artifacts.add('archives', project.tasks['debugSourcesJar'])
        }
    }

    /**
     * Sets basic bintray configuration, repository configuration, user and password.
     * Also renames the sources file so that the bintray plugin finds it and writes the valid
     * pom as the default pom so that the bintray plugin uploads it.
     *
     **/
    private void setBintrayConfig(String buildConfig){
        
        // Fixed repository URL.
        def repoURL = "http://github.com/mercadolibre/mobile-android_${getPublisherContainer().artifactId}"

        project.group = getPublisherContainer().groupId

        switch (buildConfig){
            case PUBLISH_RELESE :
                project.version = getPublisherContainer().version
                project.bintrayUpload.repoName = 'android-releases';
                project.bintrayUpload.packageVcsUrl =
                        "$repoURL/releases/tag/v${project.version}"
                project.bintrayUpload.versionVcsTag ="v${project.version}"
                break;
            case PUBLISH_EXPERIMENTAL :
                project.version = "${getPublisherContainer().version}-EXPERIMENTAL-${getTimestamp()}"
                project.bintrayUpload.repoName = 'android-experimental'
                break;
        }

        project.bintrayUpload.user = 'bintray-publisher'
        project.bintrayUpload.apiKey = '5438c410e4379f5f2955dd93514f4b452766b626'
        project.bintrayUpload.dryRun = false
        project.bintrayUpload.publish = true
        project.bintrayUpload.configurations = ['archives']
        project.bintrayUpload.userOrg = 'mercadolibre'
        project.bintrayUpload.packageName = "${getPublisherContainer().groupId}.${getPublisherContainer().artifactId}"
        project.bintrayUpload.packageIssueTrackerUrl = "$repoURL/issues"
        project.bintrayUpload.packageWebsiteUrl = repoURL
        project.bintrayUpload.versionName = "${project.version}"
        project.bintrayUpload.packagePublicDownloadNumbers = false

        // Write the correct pom for the version and artifactId being generated
        project.pom {
            version = project.version
            artifactId = getPublisherContainer().artifactId
            project {
                packaging 'aar'
                url repoURL
            }
        }.writeTo("build/poms/pom-default.xml")

        // Rename the sources file to find it.
        project.file("$project.buildDir/libs/${project.name}-sources.jar")
                .renameTo("$project.buildDir/libs/${project.name}-${project.version}-sources.jar")

    }

    /**
     * Retrieves the aar file to publish and renames it so that the bintray plugin uploads it correctly.
     * @param publishType one of 'release' or 'experimental'
     * @return new file path
     */
    private String getAarFilePath(String publishType){
        // Check if previous publish AAR exists (and delete it).
        def prevFile = project.file("$project.buildDir/outputs/aar/${project.name}.aar");
        if (prevFile.exists())
            prevFile.delete();

        // Get the AAR file and rename it (so that the bintray plugin uploads the aar to the correct path).
        File aarFile;
        switch (publishType){
            case PUBLISH_RELESE :
                aarFile = project.file("$project.buildDir/outputs/aar/${project.name}-release.aar");
                break;
            case PUBLISH_EXPERIMENTAL :
                aarFile = project.file("$project.buildDir/outputs/aar/${project.name}-debug.aar");
                break;
        }

        aarFile.renameTo("$project.buildDir/outputs/aar/${getPublisherContainer().artifactId}.aar")
        return "$project.buildDir/outputs/aar/${getPublisherContainer().artifactId}.aar"
    }

    /**
     * Creates the "publishAarLocal" task.
     */
    private void createPublishLocalTask() {
        def task = project.tasks.create 'publishAarLocal'
        task.setDescription('Publishes a new local version of the AAR library, locally on the .m2/repository directory.')
        task.dependsOn 'checkLocalDependencies', 'assembleDebug', 'debugSourcesJar' //, 'debugJavadocJar' --> // Uncomment to upload Javadocs. This is not working well so it is turned off.
        task.finalizedBy 'uploadArchives'

        task.doLast {

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file("$project.buildDir/outputs/aar/${project.name}-debug.aar"))
            project.artifacts.add('archives', project.tasks['debugSourcesJar'])

            // Uncomment the following line to upload Javadocs. This is not working well so it is turned off.
            // project.artifacts.add('archives', project.tasks['debugJavadocJar'])

            project.uploadArchives.repositories.mavenDeployer.pom.version += '-LOCAL-' + getTimestamp()
            // Point the repository to our .m2/repository directory.
            project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
                    }
                }

    /**
     * Creates the "tagVersion" task (if release).
     */
    private void createTagVersionTask() {
        def task = project.tasks.create 'tagVersion', Exec
        task.commandLine 'sh'
        project.afterEvaluate {
            task.args "-c", "git tag " + getPublisherContainer().version + "; git push --tags"
        }
        task.setDescription('Tags the library version in Git')

        task.dependsOn 'uploadArchives'
        project.tasks['uploadArchives'].finalizedBy task

        task.onlyIf {
            project.gradle.taskGraph.hasTask(project.tasks['publishAarRelease'])
        }
    }

    /**
     * Gets the current timestamp.
     * @return the current timestamp.
     */
    private String getTimestamp() {
        def sdf = new SimpleDateFormat('yyyyMMddHHmmss')
        sdf.timeZone = TimeZone.getTimeZone('UTC')
        sdf.format(new Date())
    }
}

/**
 * The 'publisher' extension.
 */
public class PublisherPluginExtension {

    /**
     * GroupId for Maven.
     */
    private String groupId

    /**
     * ArtifactId for Maven.
     */
    private String artifactId

    /**
     * Version for Maven.
     */
    private String version

    /**
     * Gets the group ID for Maven.
     * @return the group ID.
     */
    public String getGroupId() {
        return groupId
    }

    /**
     * Sets the group ID for Maven.
     * @param groupId the group ID.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId
    }

    /**
     * Gets the artifact ID for Maven.
     * @return the artifact ID.
     */
    public String getArtifactId() {
        return artifactId
    }

    /**
     * Sets the artifact ID for Maven.
     * @param artifactId the artifact ID.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId
    }

    /**
     * Gets the version for Maven.
     * @return the version.
     */
    public String getVersion() {
        return version
    }

    /**
     * Sets the version for Maven.
     * @param version the version.
     */
    public void setVersion(String version) {
        this.version = version
    }
}

/**
 * The 'publisher.[releases|experimental]Repository'.
 */
public class PublisherRepository {

    /**
     * The URL.
     */
    private String url

    /**
     * The username.
     */
    private String username

    /**
     * The password.
     */
    private String password

    /**
     * Gets the URL.
     * @return the URL.
     */
    public String getUrl() {
        return url
    }

    /**
     * Sets the URL.
     * @param url the URL.
     */
    public void setUrl(String url) {
        this.url = url
    }

    /**
     * Gets the username.
     * @return the username.
     */
    public String getUsername() {
        return username
    }

    /**
     * Sets the username.
     * @param username the username.
     */
    public void setUsername(String username) {
        this.username = username
    }

    /**
     * Gets the password.
     * @return the password.
     */
    public String getPassword() {
        return password
    }

    /**
     * Sets the password.
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password
    }
}

