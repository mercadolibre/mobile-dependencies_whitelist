package com.mercadolibre.android.gradle.library

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.GradleVersion

import java.text.SimpleDateFormat

/**
 * Created by mfeldsztejn on 5/23/17.
 */
public class JavaPlugin implements Plugin<Project> {

    /**
     * The project.
     */
    private Project project

    private static final String PUBLISH_RELEASE = "release"
    private static final String PUBLISH_EXPERIMENTAL = "experimental"
    private static final String PUBLISH_ALPHA = "alpha"
    private static final String BINTRAY_USER_ENV = "BINTRAY_USER"
    private static final String BINTRAY_KEY_ENV = "BINTRAY_KEY"
    private static final String BINTRAY_PROP_FILE = "bintray.properties"
    private static final String BINTRAY_USER_PROP = "bintray.user"
    private static final String BINTRAY_KEY_PROP = "bintray.key"
    private static final String TASK_PUBLISH_LOCAL = "publishJarLocal"
    private static final String TASK_PUBLISH_EXPERIMENTAL = "publishJarExperimental"
    private static final String TASK_PUBLISH_RELEASE = "publishJarRelease"
    private static final String TASK_PUBLISH_ALPHA = "publishJarAlpha"
    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    private static final String TASK_LOCK = "lock"
    private static final String DEPENDENCY_LOCK_PLUGIN = "nebula.plugin.dependencylock.DependencyLockPlugin"
    private static final String DEPENDENCY_LOCK_FILE_NAME = "dependencies.lock"

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {
        this.project = project

        // We could use "maven-publish" in a future. Right now, it does not support Android libraries (aar).
        project.apply plugin: 'maven'

        project.apply plugin: 'java'

        project.apply plugin: 'com.jfrog.bintray'

        this.project.configurations {
            archives {
                extendsFrom this.project.configurations.default
            }
        }

        addPublisherContainer()
        setupUploadArchivesTask()
        createAllTasks()
        addIsBeingPublishedMethod()
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
                conf.dependencies
                conf.dependencies.each { dep ->
                    if ("unspecified".equalsIgnoreCase(dep.version) && !conf.name.contains("compileOnly")) {
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
     * Creates the tasks to generate the JARs with the source code, one per variant.
     */
    private void createSourcesJarTasks() {
        def sourcesJarTask = project.tasks.create "sourcesJar", Jar
        sourcesJarTask.dependsOn project.tasks.getByName("compileJava")
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.from project.tasks.getByName("compileJava").source
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

    private void addIsBeingPublishedMethod() {
        project.metaClass.isBeingPublished() {
            for (def task : project.getGradle().getStartParameter().getTaskNames()) {
                def (moduleName, taskName) = task.tokenize(':')

                // If we execute without module (eg ./gradlew build)
                if (taskName == null) {
                    taskName = moduleName
                    moduleName = null
                }

                if (moduleName != null && moduleName == project.name && taskName != null &&
                        (taskName.contains(TASK_PUBLISH_LOCAL) ||
                                taskName == TASK_PUBLISH_EXPERIMENTAL ||
                                taskName == TASK_PUBLISH_ALPHA ||
                                taskName == TASK_PUBLISH_RELEASE)) {
                    return true;
                }

                if (task.toString().toLowerCase().contains(TASK_LOCK)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Creates all the relevant tasks for the plugin.
     */
    private void createAllTasks() {
        createSourcesJarTasks()
        createPublishLocalTasks()
        createPublishReleaseTask()
        createPublishExperimentalTask()
        createPublishAlphaTask()
        createCheckLocalDependenciesTask()
        createGetProjectVersionTask()
        resetUploadArchivesDependencies()
    }

    /**
     * Checks if a plugin is applied
     * @param plugin name
     * @return if the plugin exists in the project or not
     */
    private boolean isPluginApplied(String plugin) {
        return project.plugins.toString().contains(plugin)
    }

    /**
     * Sets up the project pom for a maven publisher or bintray publisher.
     *
     * @param pomGroup the group id that the pom will have
     * @param pomArtifact the artifact id that the pom will have
     * @param pomVersion the version the pom will have
     * @param pom if a specific pom will be written (and not the project), fill this argument. This is most probably
     * in the case you will upload archives to a maven local, where the pom is not in the project but in the maven
     * publisher.
     *
     * @return pom instance with set up.
     */
    def setUpPom(def pomGroup, def pomArtifact, def pomVersion, def pom = null) {
        def setDependencies = { def currentPom ->
            // Only check dependencies if the lock plugin is present. Else the repository just has dynamic deps..
            if (isPluginApplied(DEPENDENCY_LOCK_PLUGIN)
                    && project.file(DEPENDENCY_LOCK_FILE_NAME).exists()) {
                def json = new JsonSlurper().parse(project.file(DEPENDENCY_LOCK_FILE_NAME))
                //For now they are all release, so check in release and compile. If in a future
                //we have also for debug, change here to find the release or debug accordingly
                def deps = currentPom.generatedDependencies
                for (def dep : deps) {
                    if (dep.version.contains("+")) {
                        if (json.compile["${dep.groupId}:${dep.artifactId}"]) {
                            dep.version = json.compile["${dep.groupId}:${dep.artifactId}"].locked
                        }
                    }
                }

                // We set configurations to null to avoid generating the dependencies and having duplicated all of them
                currentPom.configurations = null

                // Since the POM wont be generating them, we put them on our own :)
                currentPom.dependencies = deps
            }
        }

        if (pom == null) {
            return project.pom {
                setDependencies it

                version = pomVersion
                artifactId = pomArtifact
                groupId = pomGroup

                project {
                    packaging 'jar'
                    url "http://github.com/mercadolibre/mobile-android_${pomArtifact}"
                }
            }
        } else {
            setDependencies pom
            pom.version = pomVersion
            pom.artifactId = pomArtifact
            pom.groupId = pomGroup
            return pom
        }
    }

    /**
     * Sets up the "uploadArchives" task from the "maven" plugin.
     */
    private void setupUploadArchivesTask() {

        project.afterEvaluate {

            validatePublisherContainer()
            project.ext.versionName = getPublisherContainer().version;
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: "file://${System.properties['user.home']}/.m2/repository")

                        setUpPom(getPublisherContainer().groupId,
                                getPublisherContainer().artifactId,
                                getPublisherContainer().version,
                                pom)
                    }
                }
            }
        }
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
     * Create and configure a bintray task for a specific
     * publishType. Take into account that this type of task
     * must be a Bintray type (not a local publish for example)
     */
    def createBintrayTask(def publishType) {
        def buildtask = project.tasks.getByName("build")
                .doFirst {
            println "building"
        }
        .doLast {
            "finished building"
        }
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
            print project.file(getAarFilePath())
            print project.file(getAarFilePath()).exists()
            project.artifacts.add('archives', project.file(getAarFilePath()))
            project.artifacts.add('archives', project.tasks['sourcesJar'])

            logVersion(String.format("%s:%s:%s", project.group, project.name, project.version))
        }
    }

    /**
     * Creates the "publishAarRelease" task.
     */
    def createPublishReleaseTask() {
        createBintrayTask(PUBLISH_RELEASE)
    }

    /**
     * Creates the "publishAarAlpha" task.
     */
    def createPublishAlphaTask() {
        createBintrayTask(PUBLISH_ALPHA)
    }

    /**
     * Creates the "publishAarExperimental" task.
     */
    def createPublishExperimentalTask() {
        createBintrayTask(PUBLISH_EXPERIMENTAL)
    }

    /**
     * Creates the "publishAarLocalX" task, where X are each of the library variants
     */
    private void createPublishLocalTasks() {
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

                project.artifacts.add('archives', project.file(getAarFilePathForLocalPublish()))

                def version = project.uploadArchives.repositories.mavenDeployer.pom.version
                project.uploadArchives.repositories.mavenDeployer.pom.version = "LOCAL-${version}-${getTimestamp()}"

                def pom = project.uploadArchives.repositories.mavenDeployer.pom
                logVersion(String.format("%s:%s:%s", pom.groupId, pom.artifactId, pom.version))

                // Point the repository to our .m2/repository directory.
                project.uploadArchives.repositories.mavenDeployer.repository.url = "file://${System.properties['user.home']}/.m2/repository"
            }
        }
    }

    /**
     * Log the given {@code library}.
     * @param library It should be the string concatenation between {@code groupId}, {@code module} and {@code version}
     */
    private static void logVersion(String library) {
        println 'Publishing library: ' + library
    }

    /**
     * Creates the "getProjectVersion" task.
     */
    private void createGetProjectVersionTask() {
        def task = project.tasks.create TASK_GET_PROJECT_VERSION
        task.setDescription('Gets project version')

        task.doLast {
            // ToDO: This is duplicated in bintraypublish.gradle ==> Wrong!

            def projectVersion = getPublisherContainer().version;

            def fileName = "project.version"
            def folder = new File('build')
            if (!folder.exists()) {
                folder.mkdirs()
            }

            def inputFile = new File("${folder}/${fileName}")
            inputFile.write("version: ${projectVersion}")
            println "See '${folder}/${fileName}' file"
        }
    }

    /**
     * Sets basic bintray configuration, repository configuration, user and password.
     * Also renames the sources file so that the bintray plugin finds it and writes the valid
     * pom as the default pom so that the bintray plugin uploads it.
     **/
    private void setBintrayConfig(String buildConfig) {

        // Fixed repository URL.
        def repoURL = "http://github.com/mercadolibre/mobile-android_${getPublisherContainer().artifactId}"

        project.group = getPublisherContainer().groupId

        switch (buildConfig) {
            case PUBLISH_RELEASE:
                project.version = getPublisherContainer().version
                project.bintrayUpload.repoName = 'android-releases';
                project.bintrayUpload.packageVcsUrl =
                        "$repoURL/releases/tag/v${project.version}"
                project.bintrayUpload.versionVcsTag = "v${project.version}"
                break
            case PUBLISH_ALPHA:
                project.version = "${getPublisherContainer().version}-ALPHA-${getTimestamp()}"
                project.bintrayUpload.repoName = 'android-releases'
                project.bintrayUpload.packageVcsUrl = "$repoURL/releases/tag/v${project.version}"
                project.bintrayUpload.versionVcsTag = "v${project.version}"
                break
            case PUBLISH_EXPERIMENTAL:
                project.version = "EXPERIMENTAL-${getPublisherContainer().version}-${getTimestamp()}"
                project.bintrayUpload.repoName = 'android-experimental'
                break
        }
        loadBintrayCredentials()
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
        setUpPom(project.group,
                getPublisherContainer().artifactId,
                project.version).writeTo("build/poms/pom-default.xml")

        // Rename the sources file to find it.
        project.file("$project.buildDir/libs/${project.name}-sources.jar")
                .renameTo("$project.buildDir/libs/${project.name}-${project.version}-sources.jar")

    }

    /**
     * Sets bintray credentials
     */
    private void loadBintrayCredentials() {
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
     * Retrieves the aar file to publish and renames it so that the bintray plugin uploads it correctly.
     * @return new file path
     */
    private String getAarFilePath() {
        def jarParentDirectory = "$project.buildDir/libs/"
        def actualDestination = jarParentDirectory + "${getPublisherContainer().artifactId}.jar"

        // Check if previous publish AAR exists (and delete it).
        def prevFile = project.file(jarParentDirectory + "${project.name}.jar");
        if (prevFile.exists()) {
            prevFile.renameTo(actualDestination)
        }

        return actualDestination
    }

    private String getAarFilePathForLocalPublish() {
        def jarParentDirectory = "$project.buildDir/libs/"
        return jarParentDirectory + "${project.name}.jar";
    }

    /**
     * Gets the current timestamp.
     * @return the current timestamp.
     */
    private static String getTimestamp() {
        def sdf = new SimpleDateFormat('yyyyMMddHHmmss')
        sdf.timeZone = TimeZone.getTimeZone('UTC')
        sdf.format(new Date())
    }

}
