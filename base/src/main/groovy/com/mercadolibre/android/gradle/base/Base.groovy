package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

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
    }

    /**
     * Avoids using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
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
}
