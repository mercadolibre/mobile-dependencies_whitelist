package com.mercadolibre.android.gradle.publisher.aar

import org.apache.commons.lang.mutable.MutableInt
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicReference

/**
 * Gradle plugin for publishing AARs after running some important tasks before:
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
 * @author Martin A. Heras
 */
public class PublisherPlugin implements Plugin<Project> {

    /**
     * The project.
     */
    private Project project;

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {

        this.project = project

        project.apply plugin: 'com.android.library'
        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

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
        getPublisherContainer().releasesRepository = new PublisherRepository()
        getPublisherContainer().experimentalRepository = new PublisherRepository()
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
        createTagVersionTask()
        createCheckLocalDependenciesTask()
        createRobolectricTestFile()
        resetUploadArchivesDependencies()
    }

    private void createRobolectricTestFile() {
        //Optimize app-example name
        project.android.sourceSets.test.java.srcDirs += "../app/build/generated/source/r/debug"

        def task = project.tasks.create 'createRobolectricFiles'
        task.setDescription('Creates \"test-project.properties\" file necessary for Robolectric unit testing.')
        task.dependsOn 'assemble'

        task.doLast {
            File file = project.file("src/main/test-project.properties")
            if (file.exists()){
                if (!file.delete()){
                    throw new GradleException("Cannot delete \"test-project.properties\" file. Check if some process is using it and close it.")
                }
            }
            file.createNewFile()

            File projectFile = project.file("src/main/project.properties")
            if (!projectFile.exists())
                projectFile.createNewFile()

            File[] tree = new File("build/intermediates/exploded-aar").listFiles()

            def path = "../../build/intermediates/exploded-aar/"
            def libCounter = new AtomicReference<Integer>()
            libCounter.set(new Integer(1))

            tree.each {File tmpFile ->
                addDirToFile(file, tmpFile, path, libCounter)
            }
        }
    }

    private void addDirToFile(File roboFile, File directory, String path, AtomicReference<Integer> dirCounter){
        File[] tree = directory.listFiles()

        def hasFiles = false

        tree.each { File file ->
            if (!file.isDirectory())
                hasFiles = true
        }

        if (hasFiles){
            roboFile.append("android.library.reference.${dirCounter.get().intValue()}=${path}${directory.name}\n")
            def oldValue = dirCounter.get().intValue()
            dirCounter.set(new Integer(oldValue + 1))
        } else {
            path += directory.name
            path += "/"
            tree.each { File file ->
                addDirToFile(roboFile, file, path, dirCounter)
            }
        }
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
                        repository(url: getPublisherContainer().releasesRepository.url) {
                            authentication(userName: getPublisherContainer().releasesRepository.username, password: getPublisherContainer().releasesRepository.password)
                        }
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

        // Publisher.releasesRepository container.
        if (getPublisherContainer().releasesRepository.url == null || getPublisherContainer().releasesRepository.url.length() == 0) {
            throw new GradleException("Property 'publisher.releasesRepository.url' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().releasesRepository.username == null || getPublisherContainer().releasesRepository.username.length() == 0) {
            throw new GradleException("Property 'publisher.releasesRepository.username' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().releasesRepository.password == null || getPublisherContainer().releasesRepository.password.length() == 0) {
            throw new GradleException("Property 'publisher.releasesRepository.password' is needed by the Publisher plugin. Please define it in the build script.")
        }

        // Publisher.experimentalRepository container.
        if (getPublisherContainer().experimentalRepository.url == null || getPublisherContainer().experimentalRepository.url.length() == 0) {
            throw new GradleException("Property 'publisher.experimentalRepository.url' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().experimentalRepository.username == null || getPublisherContainer().experimentalRepository.username.length() == 0) {
            throw new GradleException("Property 'publisher.experimentalRepository.username' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().experimentalRepository.password == null || getPublisherContainer().experimentalRepository.password.length() == 0) {
            throw new GradleException("Property 'publisher.experimentalRepository.password' is needed by the Publisher plugin. Please define it in the build script.")
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
        task.setDescription('Publishes a new release version of the AAR library.')
        task.dependsOn 'checkLocalDependencies', 'assembleRelease', 'check', 'releaseSourcesJar' //, 'releaseJavadocJar' --> // Uncomment to upload Javadocs. This is not working well so it is turned off.
        task.finalizedBy 'uploadArchives'

        task.doLast {
            // Set artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file("$project.buildDir/outputs/aar/${project.name}-release.aar"))
            project.artifacts.add('archives', project.tasks['releaseSourcesJar'])

            // Uncomment the following line to upload Javadocs. This is not working well so it is turned off.
            // project.artifacts.add('archives', project.tasks['releaseJavadocJar'])
        }
    }

    /**
     * Creates the "publishAarExperimental" task.
     */
    private void createPublishExperimentalTask() {
        def task = project.tasks.create 'publishAarExperimental'
        task.setDescription('Publishes a new experimental version of the AAR library.')
        task.dependsOn 'checkLocalDependencies', 'assembleDebug', 'debugSourcesJar' //, 'debugJavadocJar' --> // Uncomment to upload Javadocs. This is not working well so it is turned off.
        task.finalizedBy 'uploadArchives'

        task.doLast {

            // Set the artifacts.
            project.configurations.archives.artifacts.clear()
            project.artifacts.add('archives', project.file("$project.buildDir/outputs/aar/${project.name}-debug.aar"))
            project.artifacts.add('archives', project.tasks['debugSourcesJar'])

            // Uncomment the following line to upload Javadocs. This is not working well so it is turned off.
            // project.artifacts.add('archives', project.tasks['debugJavadocJar'])

            project.uploadArchives.repositories.mavenDeployer.pom.version += '-EXPERIMENTAL-' + getTimestamp()
            project.uploadArchives.repositories.mavenDeployer.repository.url = getPublisherContainer().experimentalRepository.url
            project.uploadArchives.repositories.mavenDeployer.repository.authentication.userName = getPublisherContainer().experimentalRepository.username
            project.uploadArchives.repositories.mavenDeployer.repository.authentication.password = getPublisherContainer().experimentalRepository.password
        }
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
     * Releases repository.
     */
    private PublisherRepository releasesRepository

    /**
     * Experimental repository.
     */
    private PublisherRepository experimentalRepository

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
     * Gets the releases repository.
     * @return the repository.
     */
    public PublisherRepository getReleasesRepository() {
        return releasesRepository
    }

    /**
     * Sets the releases repository.
     * @param releasesRepository the releases repository.
     */
    public void setReleasesRepository(PublisherRepository releasesRepository) {
        this.releasesRepository = releasesRepository
    }

    /**
     * Gets the experimental repository.
     * @return the experimental repository.
     */
    public PublisherRepository getExperimentalRepository() {
        return experimentalRepository
    }

    /**
     * Sets the experimental repository.
     * @param experimentalRepository the experimental repository.
     */
    public void setExperimentalRepository(PublisherRepository experimentalRepository) {
        this.experimentalRepository = experimentalRepository
    }

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
