package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

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
        setUpTestsLogging()
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
