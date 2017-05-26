package com.mercadolibre.android.gradle.library

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.text.SimpleDateFormat

/**
 * Created by mfeldsztejn on 5/26/17.
 */
abstract class LibraryPlugin implements Plugin<Project> {

    protected static final String PUBLISH_RELEASE = "release"
    protected static final String PUBLISH_EXPERIMENTAL = "experimental"
    protected static final String PUBLISH_ALPHA = "alpha"
    private static final String BINTRAY_USER_ENV = "BINTRAY_USER"
    private static final String BINTRAY_KEY_ENV = "BINTRAY_KEY"
    private static final String BINTRAY_PROP_FILE = "bintray.properties"
    private static final String BINTRAY_USER_PROP = "bintray.user"
    private static final String BINTRAY_KEY_PROP = "bintray.key"
    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    private static final String TASK_LOCK = "lock"
    private static final String DEPENDENCY_LOCK_PLUGIN = "nebula.plugin.dependencylock.DependencyLockPlugin"
    private static final String DEPENDENCY_LOCK_FILE_NAME = "dependencies.lock"

    Project project;

    @Override
    void apply(Project project) {
        this.project = project;

        applyPlugins()

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

    abstract void applyPlugins();

    abstract void createSourcesJarTasks()

    abstract boolean isPublishTask(String task)

    abstract void createPublishLocalTasks()

    abstract void createBintrayTask(String task)

    abstract String getFilePathForLocalPublish()

    abstract String getPomPackaging()

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

                if (moduleName != null && moduleName == project.name && taskName != null && isPublishTask(taskName)) {
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
     * Sets up the "uploadArchives" task from the "maven" plugin.
     */
    protected void setupUploadArchivesTask() {

        project.afterEvaluate {

            validatePublisherContainer()
            project.ext.versionName = publisherContainer.version;
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: "file://${System.properties['user.home']}/.m2/repository")

                        setUpPom(publisherContainer.groupId, publisherContainer.artifactId, publisherContainer.version, pom)
                    }
                }
            }
        }
    }

    /**
     * Validates that all the needed configuration is set within the 'publisher' container.
     */
    protected void validatePublisherContainer() {

        // Publisher container.
        if (!publisherContainer.groupId) {
            throw new GradleException("Property 'publisher.groupId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (!publisherContainer.artifactId) {
            throw new GradleException("Property 'publisher.artifactId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (!publisherContainer.version) {
            throw new GradleException("Property 'publisher.version' is needed by the Publisher plugin. Please define it in the build script.")
        }

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
     * Gets the 'publisher' container.
     * @return the 'publisher' container.
     */
    protected PublisherPluginExtension getPublisherContainer() {
        return project.publisher
    }

    /**
     * Checks if a plugin is applied
     * @param plugin name
     * @return if the plugin exists in the project or not
     */
    protected boolean isPluginApplied(String plugin) {
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
                    packaging pomPackaging
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
     * Resets the "uploadArchives" task dependencies so that we can configure its
     * artifacts in a custom way, depending on the variants.
     */
    private void resetUploadArchivesDependencies() {
        project.tasks['uploadArchives'].dependsOn.clear()
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
     * Creates the "getProjectVersion" task.
     */
    private void createGetProjectVersionTask() {
        def task = project.tasks.create TASK_GET_PROJECT_VERSION
        task.setDescription('Gets project version')

        task.doLast {
            // ToDO: This is duplicated in bintraypublish.gradle ==> Wrong!

            def projectVersion = publisherContainer.version;

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
    protected void setBintrayConfig(String buildConfig) {

        // Fixed repository URL.
        def repoURL = "http://github.com/mercadolibre/mobile-android_${publisherContainer.artifactId}"

        project.group = publisherContainer.groupId

        switch (buildConfig) {
            case PUBLISH_RELEASE:
                project.version = publisherContainer.version
                project.bintrayUpload.repoName = 'android-releases';
                project.bintrayUpload.packageVcsUrl =
                        "$repoURL/releases/tag/v${project.version}"
                project.bintrayUpload.versionVcsTag = "v${project.version}"
                break
            case PUBLISH_ALPHA:
                project.version = "${publisherContainer.version}-ALPHA-${getTimestamp()}"
                project.bintrayUpload.repoName = 'android-releases'
                project.bintrayUpload.packageVcsUrl = "$repoURL/releases/tag/v${project.version}"
                project.bintrayUpload.versionVcsTag = "v${project.version}"
                break
            case PUBLISH_EXPERIMENTAL:
                project.version = "EXPERIMENTAL-${publisherContainer.version}-${getTimestamp()}"
                project.bintrayUpload.repoName = 'android-experimental'
                break
        }
        loadBintrayCredentials()
        project.bintrayUpload.dryRun = false
        project.bintrayUpload.publish = true
        project.bintrayUpload.configurations = ['archives']
        project.bintrayUpload.userOrg = 'mercadolibre'
        project.bintrayUpload.packageName = "${publisherContainer.groupId}.${publisherContainer.artifactId}"
        project.bintrayUpload.packageIssueTrackerUrl = "$repoURL/issues"
        project.bintrayUpload.packageWebsiteUrl = repoURL
        project.bintrayUpload.versionName = "${project.version}"
        project.bintrayUpload.packagePublicDownloadNumbers = false

        // Write the correct pom for the version and artifactId being generated
        setUpPom(project.group,
                publisherContainer.artifactId,
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
     * Log the given {@code library}.
     * @param library It should be the string concatenation between {@code groupId}, {@code module} and {@code version}
     */
    protected static void logVersion(String library) {
        println 'Publishing library: ' + library
    }

    /**
     * Gets the current timestamp.
     * @return the current timestamp.
     */
    static String getTimestamp() {
        def sdf = new SimpleDateFormat('yyyyMMddHHmmss')
        sdf.timeZone = TimeZone.getTimeZone('UTC')
        sdf.format(new Date())
    }
}
