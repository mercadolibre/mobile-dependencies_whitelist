package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.GradleException

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    private static final String DEFAULT_GRADLE_WRAPPER_VERSION = '2.6'
    
    private static final String LIBRARY_PLUGIN = "com.android.build.gradle.LibraryPlugin"
    private static final String[] ALLOWED_LIBRARY_DEPENDENCIES = [ "com.mercadolibre.android.sdk" ]
    private static final String[] ALLOWED_APPLICATION_DEPENDENCIES = [ "com.mercadolibre" ]

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
        setUpLint()
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
     * Set up dependency lint to run always at the start of a task.
     *
     * This wont be triggered by adb install so wont be triggered by the
     * 'play' button in Android Studio (to let the user fasten up development
     * in the staging process)
     */
    def setUpLint() {
        project.gradle.allprojects { innerProject ->
            afterEvaluate {
                lintDependencies(innerProject)
            }
            innerProject.ext.abortDependenciesOnError = true
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

    /**
     * Returns if the project is a library
     * If no project is given as parameter (since its useful for inner projects
     * inside the root), it will use the root as project parameter.
     */
    def isLibrary(def project = this.project) {
        def found = false
        project.getProperties()['plugins'].each {
            if (it.contains(LIBRARY_PLUGIN)) {
                found = true
            }
        }

        return found
    }

    /**
     * Will be run after a project evaluation. This is, unless running from android studio
     * always at the begginning.
     * 
     * This throws GradleException if errors are found.
     */
    def lintDependencies(def project) {
        def allowedDeps = isLibrary(project) ? ALLOWED_LIBRARY_DEPENDENCIES : ALLOWED_APPLICATION_DEPENDENCIES
        def hasFailed = false
        
        /**
         * Method to check if a part of a string is contained in
         * at least one of the strings of the array
         * eg array = [ "abc", "def", "ghi" ]
         * array.containsPartOf("ab") -> true
         * array.containsPartOf("hi") -> true
         */
        String[].metaClass.containsPartOf = { string -> 
            def returnValue = false
            delegate.find {
                if (string.contains(it)) {
                    return returnValue = true
                }
                return false
            }
            return returnValue
        }

        /**
         * Closure to report a forbidden dependency as error
         */
        def report = { message ->
            if (!hasFailed) {
                println "Forbidden dependencies found:"
            }
            println message
            hasFailed = true
        }

        // Core logic
        project.configurations.each { conf ->
            conf.dependencies.each { dep ->
                def message = "<${dep.group}:${dep.name}:${dep.version}>"  
                // If its a library it can only contain dependencies from the sdk group, if its an application only from mercadolibre's group
                if (!allowedDeps.containsPartOf(dep.group) && !dep.version.equals("unspecified")) {
                    report(message)
                } 
            }
        }
        
        // Final block which throws an exception to abort gradle task if allowed
        if (hasFailed) {
            println "Please remove them from the project."
            if (project.ext.abortDependenciesOnError) {
                throw new GradleException('There are illegal dependencies in the project. Please remove them.') 
            }
        }
    }

}
