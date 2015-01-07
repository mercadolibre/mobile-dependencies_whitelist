package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {

        // For all subprojects...
        project.gradle.allprojects {

            // Avoid using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
            }

            // Set the default repositories.
            repositories {
                jcenter()
                maven {
                    url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases'
                }
                maven {
                    url 'http://maven-mobile.melicloud.com/nexus/content/repositories/experimental'
                }
            }
        }
    }
}