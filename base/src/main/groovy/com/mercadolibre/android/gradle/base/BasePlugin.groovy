package com.mercadolibre.android.gradle.base

import com.mercadolibre.android.gradle.base.modules.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    private static final String ANDROID_APPLICATION_PLUGIN = 'com.android.application'

    private static final String NEBULA_LOCK_CLASSPATH = "com.netflix.nebula/gradle-dependency-lock-plugin"

    private static final ANDROID_LIBRARY_MODULES = { ->
        return [
                new AndroidLibraryPublishableModule(),
                new RobolectricModule(),
                new AndroidJacocoModule(),
                new LintableModule()
        ]
    }

    private static final ANDROID_APPLICATION_MODULES = { ->
        return [
                new RobolectricModule(),
                new AndroidJacocoModule()
        ]
    }

    private static final JAVA_MODULES = { ->
        return [
                new JavaPublishableModule(),
                new JavaJacocoModule()
        ]
    }

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

        project.allprojects {
            // TODO This is for giving retrocompatibility with mobile-cd. Remove it when everyone has updated
            // https://github.com/mercadolibre/mobile-cd/blob/master/helpers/android_helper.rb#L39
            it.ext.versionName = it.version
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

            // Depending on added classpaths, this modules will apply plugins
            project.afterEvaluate {
                if (project.hasClasspath(NEBULA_LOCK_CLASSPATH)) {
                    new LockableModule().configure(subproject)
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

}
