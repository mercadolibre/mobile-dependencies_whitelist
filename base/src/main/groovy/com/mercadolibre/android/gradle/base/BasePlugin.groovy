package com.mercadolibre.android.gradle.base

import com.mercadolibre.android.gradle.base.modules.AndroidJacocoModule
import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.modules.AndroidLibraryTestableModule
import com.mercadolibre.android.gradle.base.modules.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.base.modules.BaseJacocoModule
import com.mercadolibre.android.gradle.base.modules.BuildScanModule
import com.mercadolibre.android.gradle.base.modules.JavaJacocoModule
import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.modules.KeystoreModule
import com.mercadolibre.android.gradle.base.modules.LintableModule
import com.mercadolibre.android.gradle.base.modules.ListProjectsModule
import com.mercadolibre.android.gradle.base.modules.LockableModule
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPlugin

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Object> {

    public static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    public static final String ANDROID_APPLICATION_PLUGIN = 'com.android.application'

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
            new ApplicationLintOptionsModule()
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
            new BuildScanModule(),
            new ListProjectsModule()
        ]
    }

    private static final SETTINGS_MODULES = {
        return [
            new BuildScanModule()
        ]
    }

    /**
     * The project.
     */
    private Project project

    /**
     * The Settings.
     */
    private Settings settings

    /**
     * Method called by Gradle when applying this plugin.
     * @param target the Gradle target which can be Gradle Settings o Gradle project
     */
    @Override
    void apply(Object target) {
        if (target instanceof Settings) {
            apply((Settings) target)
        } else if (target instanceof Project) {
            apply((Project) target)
        }
    }

    /**
     * Method called by Gradle when applying this plugin.
     * @param settings the Settings project.
     */
    void apply(Settings settings) {
        this.settings = settings

        SETTINGS_MODULES().each {
            module -> module.configure(settings)
        }

    }

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {
        this.project = project

        VersionContainer.init()

        avoidCacheForDynamicVersions()
        addHasClasspathMethod()
        setupFetchingRepositories()
        createExtensions()

        project.allprojects {
            // TODO This is for giving retro compatibility with mobile-cd. Remove it when everyone has updated
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

            subproject.plugins.withId(ANDROID_APPLICATION_PLUGIN) {
                ANDROID_APPLICATION_MODULES().each { module -> module.configure(subproject) }
            }

            // Depending on added classpath, this modules will apply plugins
            project.afterEvaluate {
                new LockableModule().configure(subproject)
                new LintableModule().configure(subproject)
            }
        }

        PROJECT_MODULES().each {
            module -> module.configure(project)
        }
    }

    private void createExtensions() {
        LintableModule.createExtension(project)
        BaseJacocoModule.createExtension(project)
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
    private void setupFetchingRepositories() {
        // This is a workaround for Gradle 5 that does not support PasswordCredentials.
        // Once all apps are on Gradle 6+, we can remove this all and leave only
        // the "repositories" block, using the method of described here:
        // https://docs.gradle.org/6.8.1/userguide/declaring_repositories.html#sec:handling_credentials
        def homePath = System.properties['user.home']
        def props = new Properties()
        File propertiesFile = new File(homePath + '/.gradle/gradle.properties')
        propertiesFile.withInputStream { props.load(it) }

        // All Nexus credentials are the same, so we choose Internal Releases credential
        def artifactsUser = props['AndroidInternalReleasesUsername']
        def artifactsPass = props['AndroidInternalReleasesPassword']

        project.allprojects {
            repositories {
                // MELI internal release libs
                maven {
                    name 'AndroidInternalReleases'
                    url 'https://android.artifacts.furycloud.io/repository/releases/'
                    credentials {
                        username artifactsUser
                        password artifactsPass
                    }
                    content {
                        // only releases
                        includeVersionByRegex('com\\.mercadolibre\\..*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                        includeVersionByRegex('com\\.mercadopago\\..*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                        includeVersionByRegex('com\\.mercadoenvios\\..*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                        includeGroup 'com.bugsnag'
                    }
                }

                // MELI public libs - these are fewer than the private ones, so we try it later
                maven {
                    name 'AndroidExternalReleases'
                    url 'https://artifacts.mercadolibre.com/repository/android-releases/'
                    content {
                        // only releases
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^((?!EXPERIMENTAL-|LOCAL-).)*$')
                    }
                }

                // MELI internal experimental libs - only used for experimental libs
                maven {
                    name 'AndroidInternalExperimental'
                    url 'https://android.artifacts.furycloud.io/repository/experimental/'
                    credentials {
                        username artifactsUser
                        password artifactsPass
                    }
                    content {
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^(.*-)?EXPERIMENTAL-.*$')
                        includeVersionByRegex('com\\.mercadopago\\.android.*', '.*', '^(.*-)?EXPERIMENTAL-.*$')
                        includeVersionByRegex('com\\.mercadoenvios\\.android.*', '.*', '^(.*-)?EXPERIMENTAL-.*$')
                    }
                }

                // only used for local published libs
                mavenLocal {
                    content {
                        includeVersionByRegex('com\\.mercadolibre\\.android.*', '.*', '^(.*-)?LOCAL-.*$')
                        includeVersionByRegex('com\\.mercadopago\\.android.*', '.*', '^(.*-)?LOCAL-.*$')
                        includeVersionByRegex('com\\.mercadoenvios\\.android.*', '.*', '^(.*-)?LOCAL-.*$')
                    }
                }

                // rest of all repositories are managed by Nexus (Google, JCenter, etc.)
                maven {
                    name 'AndroidExtra'
                    url 'https://android.artifacts.furycloud.io/repository/extra/'
                    credentials {
                        username artifactsUser
                        password artifactsPass
                    }
                }
            }
        }
    }
}