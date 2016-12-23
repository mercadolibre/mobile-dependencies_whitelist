package com.mercadolibre.android.gradle.base

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String DEFAULT_GRADLE_WRAPPER_VERSION = '2.6'

    private static final String TASK_LOCK_VERSIONS = "lockVersions"

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
        setUpTestsLogging()
        setUpLocksTask()
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

    def setUpLocksTask() {
        // For each subproject that has the lock plugin, create the task
        project.afterEvaluate {
            project.subprojects.each { subproject ->
                // Check that the subproject has the lock features. Else we dont have to lock anything
                subproject.afterEvaluate {
                    if (subproject.plugins.toListString().contains("nebula.plugin.dependencylock.DependencyLockPlugin")) {
                        // Create the lock task for the subproject
                        def innerTask = subproject.tasks.create TASK_LOCK_VERSIONS
                        innerTask.setDescription('Locks the compiled project with the current versions of its dependencies to keep them in future assembles')
                        innerTask.doLast {
                            // Generate and save the lock first.
                            println ":${subproject.name}:generateLock"
                            subproject.generateLock.execute()
                            println ":${subproject.name}:saveLock"
                            subproject.saveLock.execute()

                            // Get the lock file created and clean any ALPHA's versions that it contains
                            def file = subproject.file('dependencies.lock')
                            def inputJson = new JsonSlurper().parseText(file.text)
                            inputJson.each { variant, variantJson ->
                                if (!variant.contains("test") && !variant.contains("Test")) {
                                    variantJson.each { dependency, dependencyVersions ->
                                        if (dependencyVersions.locked //Because when compile dirs it gets transitives without locks
                                                && dependencyVersions.locked.contains("ALPHA")) {
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

                        /**
                         * Since some repositories have local modules as dependencies and they compile them
                         * locally when not publishing and when publishing they publish in a specific order
                         * (and then use the bintray dep once they are being published) we DONT lock other modules
                         * from the same repository (since we cant determine the order they will be published
                         * and we must lock before running the release tests and ci)
                         *
                         * This means that current modules wont have a x.x.+ dependency in the build.gradle
                         * (Which makes sense, since you will have them compiled locally and on the publishing
                         * you will have your specific version target :) )
                         *
                         * For more information about the dependency lock features please read:
                         * https://github.com/nebula-plugins/gradle-dependency-lock-plugin/wiki/Usage#extensions-provided
                         * Its highly recommended to never apply this closure in libraries, since we cant know
                         * the effects it may create on the CD process
                         */
                        subproject.dependencyLock {
                            dependencyFilter = { String group, String name, String version ->
                                // Dont lock the dependencies that have as group our same group id or project name
                                def isFromLocalDependency = group == project.name
                                def isFromSameGroup = subproject.hasProperty("publisher") ? group == subproject.publisher.groupId : false
                                !(isFromLocalDependency || isFromSameGroup)
                            }
                        }
                    }
                }
            }
        }

        // Root needs to have an empty task to find it, then dependencies for each subproject will fallback
        def task = project.tasks.create TASK_LOCK_VERSIONS
        task.setDescription('Locks the compiled projects with the current versions of its dependencies to keep using them in future assembles')
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
