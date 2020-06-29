package com.mercadolibre.android.gradle.base

import com.mercadolibre.android.gradle.base.modules.AndroidJacocoModule
import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.modules.AndroidLibraryTestableModule
import com.mercadolibre.android.gradle.base.modules.BuildScanModule
import com.mercadolibre.android.gradle.base.modules.KotlinCheckModule
import com.mercadolibre.android.gradle.base.modules.JavaJacocoModule
import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.modules.KeystoreModule
import com.mercadolibre.android.gradle.base.modules.LintableModule
import com.mercadolibre.android.gradle.base.modules.LockableModule
import com.mercadolibre.android.gradle.base.modules.PackageModule
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    public static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    public static final String ANDROID_APPLICATION_PLUGIN = 'com.android.application'
    public static final String KOTLIN_ANDROID_PLUGIN = 'kotlin-android'

    private static final String BINTRAY_UPLOAD_TASK_NAME = "bintrayUpload"

    private static final ANDROID_LIBRARY_MODULES = { ->
        return [
            new AndroidLibraryPublishableModule(),
            new AndroidLibraryTestableModule(),
            new AndroidJacocoModule()
        ]
    }

    private static final ANDROID_APPLICATION_MODULES = { ->
        return [
            new AndroidJacocoModule(),
            new KeystoreModule(),
            new PackageModule()
        ]
    }

    private static final JAVA_MODULES = { ->
        return [
            new JavaPublishableModule(),
            new JavaJacocoModule()
        ]
    }

    private static final PROJECT_MODULES = { ->
        return [
            new BuildScanModule()
        ]
    }

    private static final KOTLIN_MODULES = { ->
        return [
            new KotlinCheckModule()
        ]
    }

    /**
     * The project.
     */
    private Project project

    private boolean doesTaskGraphHasPublishableTasks = false

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    @Override
    void apply(Project project) {
        this.project = project

        VersionContainer.init()

        avoidCacheForDynamicVersions()
        addHasClasspathMethod()
        setupRepositories()
        createExtensions()

        project.allprojects {
            // TODO This is for giving retrocompatibility with mobile-cd. Remove it when everyone has updated
            // https://github.com/mercadolibre/mobile-cd/blob/master/helpers/android_helper.rb#L39
            afterEvaluate {
                it.ext.versionName = it.version
            }
        }

        project.subprojects.each { Project subproject ->
            // Apply modules depending on already applied plugins
            subproject.plugins.withType(JavaPlugin) {
                JAVA_MODULES().each { module -> module.configure(subproject) }
            }

            subproject.plugins.withId(ANDROID_LIBRARY_PLUGIN) {
                ANDROID_LIBRARY_MODULES().each { module -> module.configure(subproject) }
            }

            subproject.plugins.withId(KOTLIN_ANDROID_PLUGIN) {
                KOTLIN_MODULES().each { module -> module.configure(subproject) }
            }

            subproject.plugins.withId(ANDROID_APPLICATION_PLUGIN) {
                ANDROID_APPLICATION_MODULES().each { module -> module.configure(subproject) }

                fixFindbugsDuplicateDexEntryWithJSR305(subproject)
            }

            // Depending on added classpaths, this modules will apply plugins
            project.afterEvaluate {
                new LockableModule().configure(subproject)
                new LintableModule().configure(subproject)
            }

            // We are disabling findbugs because it imposes a huge latency in the running builds
            // and its not providing any real benefit that other SCA tools already do
            subproject.tasks.configureEach { task ->
                if (task.name.toLowerCase().contains('findbugs')) {
                    task.enabled = false
                }
            }
        }

        PROJECT_MODULES().each {
            module -> module.configure(project)
        }

        project.gradle.taskGraph.whenReady { taskGraph ->
            doesTaskGraphHasPublishableTasks = checkIfTaskGraphHasPublishableTasks(project.gradle.taskGraph)
        }

        // We ensure all artifacts are published
        project.gradle.buildFinished { buildResult ->
            if (!buildResult.failure && doesTaskGraphHasPublishableTasks) {
                println 'Publishing artifacts to bintray'
                project.tasks.bintrayPublish.taskAction()
            }
        }
    }

    boolean checkIfTaskGraphHasPublishableTasks(def taskGraph) {
        return taskGraph.getAllTasks().any { task -> task.getName() == BINTRAY_UPLOAD_TASK_NAME }
    }

    /**
     * This method, as the name suggests fixes a findbugs duplicate dex entry with JSR305.
     * Info:
     * - Findbugs adds in its plugin an own JSR305 annotations (This means annotations such as
     * CheckForNull) are added not only by the JavaPlugin but also the exact same (but in a different
     * group:artifact) by findbugs.
     * - In .dex files, this cant happen, since it will create an exact same (or duplicate) entry in its
     * vtable. Hence we are forced to remove one of the two from the applications
     * - This happens because when the POM is built, findbugs dependency is added in a provided
     * configuration, making it resolvable for all flavors (as a provided/compileOnly way ofc).
     * Because of this, we are including it as provided in the POM file, but making it compilable for an APK,
     * creating the issue
     *
     * TODO: This should be removed when SCA stops including findbugs.
     */
    void fixFindbugsDuplicateDexEntryWithJSR305(Project project) {
        project.configurations {
            all*.exclude module:"jsr305"
            all*.exclude module:"jcip-annotations"
        }
    }

    private void createExtensions() {
        LintableModule.createExtension(project)
    }

    /**
     * Avoid using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
     */
    private void avoidCacheForDynamicVersions() {
        // For all sub-projects...
        project.allprojects {
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor 2, 'hours'
            }
        }
    }

    private void addHasClasspathMethod() {
        project.metaClass.hasClasspath = { String path ->
            boolean found = false
            delegate.buildscript.configurations.classpath.each { classpath ->
                if (classpath.path.contains(path)) {
                    found = true
                }
            }
            return found
        }
    }

    /**
     * Sets up the repositories.
     */
    private void setupRepositories() {
        project.allprojects {
            repositories {
                // Google libs
                google {
                    content {
                        includeGroupByRegex 'android\\.arch\\..*'
                        includeGroupByRegex 'androidx\\..*'
                        includeGroupByRegex 'com\\.android.*'
                        includeGroupByRegex 'com\\.google\\..*'
                    }
                }

                // Meli internal release libs
                maven {
                    url 'https://mercadolibre.bintray.com/android-releases'
                    credentials {
                        username 'bintray-read-only'
                        password 'e7b8b22a0b84527c04194c31f90bc0b879d8fd9d'
                    }
                    content {
                        // only releases
                        includeVersionByRegex('com\\.mercadolibre\\..*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                        includeVersionByRegex('com\\.mercadopago\\..*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                        includeGroup 'com.bugsnag'
                    }
                }

                // Meli public libs - these are fewer than the private ones, so we try it later
                maven {
                    url 'https://mercadolibre.bintray.com/android-public'
                    content {
                        // only releases
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                    }
                }

                // only for datami SDK
                maven {
                    url 'https://s3.amazonaws.com/sdk-ga-releases.cloudmi.datami.com/android/mvn/smisdk/'
                    content {
                        includeGroup 'com.datami'
                    }
                }

                // only used for github repositories
                maven {
                    url 'https://jitpack.io'
                    content {
                        includeGroupByRegex 'com\\.github\\..*'
                    }
                }

                // only used for experimental libs
                maven {
                    url 'https://mercadolibre.bintray.com/android-experimental'
                    credentials {
                        username 'bintray-read-only'
                        password 'e7b8b22a0b84527c04194c31f90bc0b879d8fd9d'
                    }
                    content {
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^(.*-)?EXPERIMENTAL-.*$')
                        includeVersionByRegex('com\\.mercadopago\\.android.*', '.*', '^(.*-)?EXPERIMENTAL-.*$')
                    }
                }

                // only used for local published libs
                mavenLocal {
                    content {
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^(.*-)?LOCAL-.*$')
                        includeVersionByRegex('com\\.mercadopago\\.android.*', '.*', '^(.*-)?LOCAL-.*$')
                    }
                }

                // catch all repositories
                jcenter()
            }
        }
    }
}