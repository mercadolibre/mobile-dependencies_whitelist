package com.mercadolibre.android.gradle.publisher.jar

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Jar

/**
 * Gradle plugin for publishing JARs after running some important tasks before:
 *
 * <ol>
 *     <li>Apply java plugin.</li>
 *     <li>Apply maven plugin.</li>
 *     <li>Generate JAR with sources.</li>
 *     <li>Generate HTMLs with Javadoc.</li>
 *     <li>Generate JAR with Javadoc.</li>
 *     <li>Run the unit tests.</li>
 *     <li>Generate Jacoco reports for code coverage.</li>
 *     <li>Upload the artifacts to the repository (either release, snapshot or local).</li>
 *     <li>Tag the version in Git (if release).</li>
 * </ol>
 *
 * @author Martin A. Heras
 */
class PublisherPlugin implements Plugin<Project> {

    private Project project;

    void apply(Project project) {

        this.project = project;

        project.apply plugin: 'java'
        // We could use "maven-publish" in a future. Right now, it is an incubating plugin.
        project.apply plugin: 'maven'
        project.apply plugin: 'jacoco'

        addPublisherContainer()
        setupUploadArchivesTask()
        createAllTasks()
        setupArtifacts()
        setupJacoco()
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
        getPublisherContainer().snapshotsRepository = new PublisherRepository()
    }

    /**
     * Creates all the relevant tasks for the plugin.
     */
    private void createAllTasks() {
        createJavadocJarTask()
        createSourcesJarTask()
        createPublishLocalTask()
        createPublishReleaseTask()
        createPublishSnapshotTask()
        createTagVersionTask()
        createRunCodeCoverageTask()
    }

    /**
     * Creates the task to generate the JAR with the Javadoc.
     */
    private void createJavadocJarTask() {
        def javadocJarTask = project.tasks.create "javadocJar", Jar
        javadocJarTask.dependsOn 'javadoc'
        javadocJarTask.classifier = 'javadoc'
        javadocJarTask.from 'build/docs/javadoc'
    }

    /**
     * Creates the task to generate the JAR with the source code.
     */
    private void createSourcesJarTask() {
        def sourcesJarTask = project.tasks.create "sourcesJar", Jar
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.from project.sourceSets.main.allSource
    }

    /**
     * Sets up the artifacts to be published.
     */
    private void setupArtifacts() {
        project.artifacts {
            archives project.tasks['jar']
            archives project.tasks['javadocJar']
            archives project.tasks['sourcesJar']
        }
    }

    /**
     * Sets up Jacoco.
     */
    private void setupJacoco() {
        project.jacoco {
            toolVersion = '0.7.1.201405082137'
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
                        // If the artifact ends with "-SNAPSHOT", this repository is selected:
                        snapshotRepository(url: getPublisherContainer().snapshotsRepository.url) {
                            authentication(userName: getPublisherContainer().snapshotsRepository.username, password: getPublisherContainer().snapshotsRepository.password)
                        }
                        pom.groupId = getPublisherContainer().groupId
                        pom.artifactId = getPublisherContainer().artifactId
                        pom.version = getPublisherContainer().version
                    }
                }
            }
        }

        project.uploadArchives.dependsOn 'runCodeCoverage'
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

        // Publisher.snapshotsRepository container.
        if (getPublisherContainer().snapshotsRepository.url == null || getPublisherContainer().snapshotsRepository.url.length() == 0) {
            throw new GradleException("Property 'publisher.snapshotsRepository.url' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().snapshotsRepository.username == null || getPublisherContainer().snapshotsRepository.username.length() == 0) {
            throw new GradleException("Property 'publisher.snapshotsRepository.username' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (getPublisherContainer().snapshotsRepository.password == null || getPublisherContainer().snapshotsRepository.password.length() == 0) {
            throw new GradleException("Property 'publisher.snapshotsRepository.password' is needed by the Publisher plugin. Please define it in the build script.")
        }

    }

    /**
     * Creates the task to run the tests and generate the Jacoco reports.
     */
    private void createRunCodeCoverageTask() {
        def task = project.tasks.create 'runCodeCoverage'
        task.setDescription('Runs the unit tests and generates the Jacoco reports for code coverage.')
        task.dependsOn 'test'
        task.dependsOn 'jacocoTestReport'
    }

    /**
     * Creates the "publishJarRelease" task.
     */
    private void createPublishReleaseTask() {
        def task = project.tasks.create 'publishJarRelease'
        task.setDescription('Publishes a new release version of the JAR library.')
        task.dependsOn 'assemble'
        task.dependsOn 'check'
        task.finalizedBy 'uploadArchives'
    }

    /**
     * Creates the "publishJarSnapshot" task.
     */
    private void createPublishSnapshotTask() {
        def task = project.tasks.create 'publishJarSnapshot'
        task.setDescription('Publishes a new snapshot version of the JAR library.')
        task.dependsOn 'assemble'
        task.finalizedBy 'uploadArchives'

        task.doLast {
            // Append "-SNAPSHOT" to the version so that it gets uploaded to the snapshots repository.
            project.uploadArchives.repositories.mavenDeployer.pom.version += '-SNAPSHOT'
        }
    }

    /**
     * Creates the "publishJarLocal" task.
     */
    private void createPublishLocalTask() {
        def task = project.tasks.create 'publishJarLocal'
        task.setDescription('Publishes a new snapshot version of the JAR library, but locally on the .m2/repository directory.')
        task.dependsOn 'assemble'
        task.finalizedBy 'uploadArchives'

        task.doLast {
            // Append "-SNAPSHOT" to the version so that it gets uploaded to the snapshots repository.
            project.uploadArchives.repositories.mavenDeployer.pom.version += '-SNAPSHOT'
            // Point the repository to our .m2/repository directory.
            project.uploadArchives.repositories.mavenDeployer.snapshotRepository.url = "file://${System.properties['user.home']}/.m2/repository"
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
            project.gradle.taskGraph.hasTask(project.tasks['publishJarRelease'])
        }
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
     * Snapshots repository.
     */
    private PublisherRepository snapshotsRepository

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
     * Gets the snapshots repository.
     * @return the snapshots repository.
     */
    public PublisherRepository getSnapshotsRepository() {
        return snapshotsRepository
    }

    /**
     * Sets the snapshots repository.
     * @param snapshotsRepository the snapshots repository.
     */
    public void setSnapshotsRepository(PublisherRepository snapshotsRepository) {
        this.snapshotsRepository = snapshotsRepository
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
 * The 'publisher.[releases|snapshots]Repository'.
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