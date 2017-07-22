package com.mercadolibre.android.gradle.base

import com.mercadolibre.android.gradle.base.modules.AndroidJacocoModule
import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.modules.JavaJacocoModule
import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.modules.LintableModule
import com.mercadolibre.android.gradle.base.modules.LockableModule
import com.mercadolibre.android.gradle.base.modules.Module
import com.mercadolibre.android.gradle.base.modules.RobolectricModule
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    private static final String ANDROID_APPLICATION_PLUGIN = 'com.android.application'

    private static final String NEBULA_LOCK_CLASSPATH = "com.netflix.nebula/gradle-dependency-lock-plugin"
    private static final String LINT_PLUGIN_CLASSPATH= "com.mercadolibre.android.gradle/lint"

    private static final Module[] ANDROID_LIBRARY_MODULES = [
            new AndroidLibraryPublishableModule(),
            new RobolectricModule(),
            new AndroidJacocoModule()
    ]

    private static final Module[] ANDROID_APPLICATION_MODULES = [
            new RobolectricModule(),
            new AndroidJacocoModule()
    ]

    private static final Module[] JAVA_MODULES = [
            new JavaPublishableModule(),
            new JavaJacocoModule()
    ]

    /**
     * The project.
     */
    private Project project

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {
        this.project = project

        avoidCacheForDynamicVersions()
        addHasClasspathMethod()
        setupRepositories()

        setUpTestsLogging()

        project.subprojects.each {
            if (it.plugins.withType(JavaPlugin)) {
                JAVA_MODULES.each { module -> module.configure(it) }
            }

            if (it.pluginManager.hasPlugin(ANDROID_LIBRARY_PLUGIN)) {
                ANDROID_LIBRARY_MODULES.each { module -> module.configure(it) }
            }

            if (it.pluginManager.hasPlugin(ANDROID_APPLICATION_PLUGIN)) {
                ANDROID_APPLICATION_MODULES.each { module -> module.configure(it) }
            }

            project.afterEvaluate {
                if (project.hasClasspath(NEBULA_LOCK_CLASSPATH)) {
                    new LockableModule().configure(it)
                }

                if (project.hasClasspath(LINT_PLUGIN_CLASSPATH) && it.pluginManager.hasPlugin(ANDROID_LIBRARY_PLUGIN)) {
                    new LintableModule().configure(it)
                }
            }
        }
    }

    /**
     * Avoid using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
     */
    private void avoidCacheForDynamicVersions() {
        // For all sub-projects...
        project.gradle.allprojects {
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor 30, 'minutes'
            }
        }
    }

    private void addHasClasspathMethod() {
        project.metaClass.hasClasspath = { String path ->
            delegate.buildscript.configurations.classpath.each { classpath ->
                if (classpath.path.contains(path)) {
                    return true
                }
            }
            return false
        }
    }

    /**
     * Sets up the repositories.
     */
    private void setupRepositories() {
        project.allprojects {
            repositories {
                jcenter()
                mavenLocal()
                maven {
                    url 'https://maven.google.com'
                }
                maven {
                    url "https://dl.bintray.com/mercadolibre/android-releases"
                    credentials {
                        username 'bintray-read'
                        password 'ff5072eaf799961add07d5484a6283eb3939556b'
                    }
                }
                maven {
                    url "https://dl.bintray.com/mercadolibre/android-experimental"
                    credentials {
                        username 'bintray-read'
                        password 'ff5072eaf799961add07d5484a6283eb3939556b'
                    }
                }
            }
        }
    }

    /**
     * Setup unit tests logging to log only failed tests to keep output clean.
     */
    private void setUpTestsLogging() {
        project.subprojects.collect {
            it.tasks.withType(Test) {
                testLogging {
                    events "FAILED"
                    exceptionFormat "full"
                }
            }
        }
    }

}
