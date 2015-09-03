package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String DEFAULT_GRADLE_WRAPPER_VERSION = '2.6'

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

        avoidCacheForDynamicVersions()
        setupRepositories()
        setDefaultGradleVersion()
    }

    /**
     * Avoid using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
     */
    private void avoidCacheForDynamicVersions() {
        // For all sub-projects...
        project.gradle.allprojects {
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
            }
        }
    }
    /**
     * Sets up the repositories.
     */
    private void setupRepositories() {
        // For all sub-projects...
        project.gradle.allprojects {
            repositories {
                jcenter()
                mavenLocal()
                mavenCentral()

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
     * Set the default Gradle version based on {@link #DEFAULT_GRADLE_WRAPPER_VERSION}.
     * <p/>
     * <strong>Note that to start using this version, the user must run {code ./gradlew wrapper} to update the wrapper.</strong>
     */
    void setDefaultGradleVersion() {
        def wrapperTask = project.tasks.findByName("wrapper")
        if (wrapperTask == null) {
            println 'ERROR: Unable to set default Gradle version to: ' + DEFAULT_GRADLE_WRAPPER_VERSION
        } else {
            wrapperTask.gradleVersion = DEFAULT_GRADLE_WRAPPER_VERSION
        }
    }
}
