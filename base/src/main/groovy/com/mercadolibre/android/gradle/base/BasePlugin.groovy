package com.mercadolibre.android.gradle.base

import com.mercadolibre.android.gradle.base.modules.LintableModule
import com.mercadolibre.android.gradle.base.modules.LockableModule
import com.mercadolibre.android.gradle.base.modules.PublishableModule
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String JAVA_PLUGIN = 'java'
    private static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    private static final String NEBULA_LOCK_CLASSPATH = "com.netflix.nebula/gradle-dependency-lock-plugin"
    private static final String LINT_PLUGIN_CLASSPATH= "com.mercadolibre.android.gradle/lint"

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

        def testCoverage = new TestCoverage()
        testCoverage.createJacocoFinalProjectTask(project)
        testCoverage.createCoveragePost(project)

        project.subprojects.each {
            if (it.pluginManager.hasPlugin(JAVA_PLUGIN) || it.pluginManager.hasPlugin(ANDROID_LIBRARY_PLUGIN)) {
                new PublishableModule().configure(it)
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
