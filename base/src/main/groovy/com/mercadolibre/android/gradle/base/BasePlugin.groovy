package com.mercadolibre.android.gradle.base

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.GradleException

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String DEFAULT_GRADLE_WRAPPER_VERSION = '2.6'

    private static final String TASK_LOCK_VERSIONS = "lockVersions"

    private static final String LIBRARY_PLUGIN_NAME = "com.android.build.gradle.LibraryPlugin"

    private static final String LINT_PLUGIN_CLASSPATH= "com.mercadolibre.android.gradle/lint"
    private static final String LINT_PLUGIN_NAME = "com.mercadolibre.android.gradle.lint"

    private static final String NEBULA_LOCK_CLASSPATH = "com.netflix.nebula/gradle-dependency-lock-plugin"
    private static final String NEBULA_LOCK_PLUGIN_NAME = 'nebula.dependency-lock'
    private static final String NEBULA_LOCK_DEFAULT_FILE_NAME = 'dependencies.lock'
    private static final String[] NEBULA_LOCK_TASKS = [
        "generateLock",
        "saveLock"
    ]

    private static final String VERSION_ALPHA = "ALPHA"

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
        setupRepositories()
        setDefaultGradleVersion()
        setUpTestsLogging()
        setUpLocksTask()
        setUpLintPlugin()
        def testCoverage = new TestCoverage()
        testCoverage.createJacocoFinalProjectTask(project)
        testCoverage.createCoveragePost(project)
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
     * Apply lint plugin to all subprojects that are library.
     *
     * This will be done if (and only if) the lint classpath is present in the root project 
     */
    private void setUpLintPlugin() {
        def pluginExists = false
        // Check that the project supports plugin features. Else we wont add it
        project.afterEvaluate {
            project.buildscript.configurations.classpath.each { classpath ->
                if (classpath.path.contains(LINT_PLUGIN_CLASSPATH)) {
                    pluginExists = true
                }
            }

            // If it supports it, then add the plugin for each of the subprojects.
            if (pluginExists) {
                project.subprojects.each { subproject ->
                    def isLibrary = false
                    // Check the project is a library!
                    subproject.plugins.each { plugin ->
                        if (plugin.toString().contains(LIBRARY_PLUGIN_NAME)) {
                            isLibrary = true
                        }
                    }
                    if (isLibrary) {
                        subproject.apply plugin: LINT_PLUGIN_NAME
                    }
                }
            }
        }
    }

    /**
     * Set up lock task for each subproject that has the nebula plugin included.
     * Also sets up an empty task for the root project, so we can call it without the need of a module
     * to call all of the subproject tasks at the same time (eg ./gradlew lockVersions -> each module 'lockVersions')
     */
    private void setUpLocksTask() {
        def dependencyLockPluginExists = false
        // Check that the project supports lock features. Else we wont add it
        project.afterEvaluate {
            project.buildscript.configurations.classpath.each { classpath ->
                if (classpath.path.contains(NEBULA_LOCK_CLASSPATH)) {
                    dependencyLockPluginExists = true
                }
            }

            // If it supports locks, then add the task for each of the subprojects.
            if (dependencyLockPluginExists) {
                project.subprojects.each { subproject ->
                    subproject.apply plugin: NEBULA_LOCK_PLUGIN_NAME

                    subproject.afterEvaluate {
                        // Create the lock task for the subproject
                        def innerTask = subproject.tasks.create TASK_LOCK_VERSIONS
                        innerTask.setDescription('Locks the compiled project with the current versions of its dependencies to keep them in future assembles')
                        innerTask.doLast {
                            // Generate and save the lock first.
                            NEBULA_LOCK_TASKS.each {
                                println ":${subproject.name}:${it}"
                                subproject.tasks[it].execute()
                            }


                            println ":${subproject.name}:filterLock"
                            // Get the lock file created and clean any ALPHA's versions that it contains
                            def file = subproject.file(NEBULA_LOCK_DEFAULT_FILE_NAME)
                            def inputJson = new JsonSlurper().parseText(file.text)
                            inputJson.each { variant, variantJson ->        
                                if (!variant.contains("test") && !variant.contains("Test")) {
                                    variantJson.each { dependency, dependencyVersions ->
                                        if (dependencyVersions.locked //Because when compile dirs it gets transitives without locks
                                                && dependencyVersions.locked.contains(VERSION_ALPHA)) {
                                            dependencyVersions.locked = dependencyVersions.locked.find(/.*\..*\.[0-9]+/)
                                            // Accepts [everything].[everything].[only numbers]
                                        }
                                    }
                                }
                            }

                            // Write the new file
                            def jsonBuilder = new JsonBuilder(inputJson)
                            file.withWriter {
                                it.write jsonBuilder.toPrettyString()
                            }
                        }
                    }
                }

                // Root needs to have an empty task to find it, then dependencies for each subproject will fallback
                def task = project.tasks.create TASK_LOCK_VERSIONS
                task.setDescription('Locks the compiled projects with the current versions of its dependencies to keep using them in future assembles')
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
     * Upgrade the default Gradle version based on {@link #DEFAULT_GRADLE_WRAPPER_VERSION} when corresponding.
     * <p/>
     * <strong>Note that to start using this version, the user must run {@code ./gradlew wrapper} to update the wrapper.</strong>
     * <p/>
     * Users can also override this default version by adding the following code block to the root {@code build.gradle}:
     * <pre>
     *     wrapper {
     *         gradleVersion = '3.0'
     *     }
     * </pre>
     */
    void setDefaultGradleVersion() {
        def wrapperTask = project.tasks.findByName("wrapper")

        if (wrapperTask == null) {
            println 'ERROR: Unable to set default Gradle version to: ' + DEFAULT_GRADLE_WRAPPER_VERSION

        } else if (shouldUpgradeGradleWrapper(wrapperTask.gradleVersion)) {
            wrapperTask.gradleVersion = DEFAULT_GRADLE_WRAPPER_VERSION

            println ':' + wrapperTask.name
            wrapperTask.execute()
            println 'Gradle Wrapper version upgraded to: ' + wrapperTask.gradleVersion
        }
    }

    /**
     * Check whether we should upgrade Gradle Wrapper's version or not.
     * @param wrapperVersion
     * @return <code>true</code> to upgrade it. Otherwise <code>false</code>.
     */
    static boolean shouldUpgradeGradleWrapper(String wrapperVersion) {
        return !DEFAULT_GRADLE_WRAPPER_VERSION.equals(wrapperVersion) && DEFAULT_GRADLE_WRAPPER_VERSION.equals(getGreater(DEFAULT_GRADLE_WRAPPER_VERSION, wrapperVersion))
    }

    /**
     * Compare two version numbers.
     * <strong>Important: </strong>It only supports version numbers containing only numbers.
     *
     * @param aVersion
     * @param bVersion
     * @return The greater one between aVersion and bVersion
     */
    static String getGreater(String aVersion, String bVersion) {
        String[] aArray = aVersion.split("[.]")
        String[] bArray = bVersion.split("[.]")

        String result

        int length = aArray.length > bArray.length ? aArray.length : bArray.length
        for (int i = 0; i < length; i++) {

            float aToken
            try {
                aToken = Float.valueOf(aArray[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing.
            }

            float bToken
            try {
                bToken = Float.valueOf(bArray[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing.
            }

            if (!bToken || aToken > bToken) {
                result = aVersion
                break

            } else if (!aToken || aToken < bToken) {
                result = bVersion
                break
            } else {
                // Do nothing
            }
        }

        return result
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
