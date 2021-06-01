package com.mercadolibre.android.gradle.base.lint.dependencies

import com.mercadolibre.android.gradle.base.BasePlugin
import com.mercadolibre.android.gradle.base.lint.Lint
import groovy.json.JsonSlurper
import org.gradle.api.Project

/**
 * Class that lints the dependencies in the project checking that it only
 * compiles the whitelisted ones
 */
class LibraryWhitelistedDependenciesLint implements Lint {

    private static final String ERROR_TITLE = "ERROR: The following dependencies are not allowed:"
    private static final String ERROR_ALLOWED_DEPENDENCIES_PREFIX = "\nYour project can only contain the" +
        " dependencies listed in:"
    private static final String ERROR_ALLOWED_DEPENDENCIES_SUFFIX = "If you think one of them should be in" +
        " the whitelist, please start here https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas\n"

    private static final String FILE_BLOCKER = "build/reports/${LibraryWhitelistedDependenciesLint.class.simpleName}/${Lint.LINT_FILENAME}"
    private static final String FILE_WARNING = "build/reports/${LibraryWhitelistedDependenciesLint.class.simpleName}/${Lint.LINT_WARNING_FILENAME}"

    // Generated by Gradle/Maven by default for a project
    private static final String DEFAULT_GRADLE_VERSION_VALUE = "unspecified"

    private boolean hasFailed = false
    private Project project

    /**
     * Array with whitelisted dependencies
     */
    List<Dependency> WHITELIST_DEPENDENCIES = new ArrayList<Dependency>()
    List<Dependency> WHITELIST_GOING_TO_EXPIRE = new ArrayList<Dependency>()

    /**
     * Checks the dependencies the project contains are in the whitelist
     */
    boolean lint(Project project, def variants) {
        this.project = project
        if (!project.lintGradle.dependenciesLintEnabled) {
            return false
        }

        if (project.plugins.hasPlugin(BasePlugin.ANDROID_LIBRARY_PLUGIN)) {
            setUpWhitelist(project.lintGradle.dependencyWhitelistUrl as String)

            // This is a new run, so remove the file if it exists, we will override it
            if (project.file(FILE_BLOCKER).exists()) {
                project.file(FILE_BLOCKER).delete()
            }

            // Check dependencies of each variant available first
            variants.each { variant ->
                String variantName = variant.name
                project.configurations."${variantName}Api".dependencies.each {
                    analizeDependency(it)
                }
                project.configurations."${variantName}Implementation".dependencies.each {
                    analizeDependency(it)
                }
                project.configurations."${variantName}Compile".dependencies.each {
                    analizeDependency(it)
                }
            }

            // Check the default compiling deps
            project.configurations.api.dependencies.each {
                analizeDependency(it)
            }
            project.configurations.implementation.dependencies.each {
                analizeDependency(it)
            }
            project.configurations.compile.dependencies.each {
                analizeDependency(it)
            }

            if (hasFailed) {
                report("${ERROR_ALLOWED_DEPENDENCIES_PREFIX} ${project.lintGradle.dependencyWhitelistUrl}\n" +
                    "${ERROR_ALLOWED_DEPENDENCIES_SUFFIX}")
            }

            if (WHITELIST_GOING_TO_EXPIRE.size() > 0) {
                reportWarnings()
            }
        }

        return hasFailed
    }

    private void reportWarnings() {
        File file = project.file(FILE_WARNING)
        // Create the file
        if (project.file(FILE_WARNING).exists()) {
            project.file(FILE_WARNING).delete()
        } else {
            file.getParentFile().mkdirs()
        }

        String message = "WARNING: The following dependencies has been marked as deprecated: \n"
        WHITELIST_GOING_TO_EXPIRE.each {
            message += "(${findDependencyInList(it, WHITELIST_DEPENDENCIES)?.rawExpiresDate}) - ${it} (Deprecated!)\n"
        }
        message += "\nYou should consider upgrading the lib OR contact the team owner to know how to proceed.\n"
        println message
        file.append(message)
    }

    /**
     * Searches the dependency from a list
     * @param dependency string full name description
     * @param list in what list search
     * @return null or the matched dependency
     */
    private Dependency findDependencyInList(String dependency, ArrayList list) {
        for (Dependency whitelistDep : list) {
            if (dependency =~ /${whitelistDep.group}:${whitelistDep.name}:(${whitelistDep.version})/) {
                return whitelistDep
            }
        }
        return null
    }

    /**
     * report a forbidden dependency as error
     */
    private void report(message) {
        File file = project.file(FILE_BLOCKER)
        // This will happen only the first time (since the first time it hasnt 'already failed'
        if (!hasFailed) {
            // Create the file
            if (!file.exists()) {
                file.getParentFile().mkdirs()
            }
            // Flag it as failed and write to the stdout and file output.
            hasFailed = true
            println "\n" + ERROR_TITLE
            file << ERROR_TITLE
        }

        // Write file and stdout with message
        file.append("${System.getProperty("line.separator")}${message}")
        println message
    }

    // Core logic
    private void analizeDependency(dependency) {
        String dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"
        boolean isLocalModule = project.rootProject.allprojects.find {
            dependencyFullName.contains("${it.group}:${it.name}")
        } != null

        /**
         * - Dependency cant be found in whitelist
         * - If Isn't "unspecified" the name of the dependency
         * - And Dependency isn't from the same group (you CAN compile dependencies from your own modules)
         * Only if all of the above meet it will error.
         */
        if (!dependencyFullName.contains(DEFAULT_GRADLE_VERSION_VALUE) && !isLocalModule) {
            Status result = getStatusDependencyInWhitelist(dependencyFullName)
            if (result.isBlocker()) {
                report(result.message(dependencyFullName))
            } else if (result.reportable()) {
                WHITELIST_GOING_TO_EXPIRE.add(dependencyFullName)
            }
        }
    }

    /**
     * Returns the task name
     */
    String name() {
        return "lintDependencies"
    }

    /**
     * Method to check if a dependency exists in the whitelist.
     * @returns Status notifying the result
     */
    Status getStatusDependencyInWhitelist(String dependency) {
        Dependency dep = findDependencyInList(dependency, WHITELIST_DEPENDENCIES)

        if (dep) {
            if (dep.expires == Long.MAX_VALUE) {
                return Status.AVAILABLE
            } else if (System.currentTimeMillis() < dep.expires) {
                return Status.GOING_TO_EXPIRE
            } else {
                return Status.EXPIRED
            }
        }
        return Status.INVALID
    }

    /**
     * Sets up the whitelist, this will get a json from the whitelistUrl defined
     * and parse the formatted JSON into a list of dependencies
     * @param whitelistUrl well formed url with JSON content
     */
    void setUpWhitelist(String whitelistUrl) {
        new URL(whitelistUrl).openConnection().with { conn ->
            def jsonSlurper = new JsonSlurper().parseText(conn.inputStream.text)
            jsonSlurper.whitelist.each { dependency ->
                WHITELIST_DEPENDENCIES.add(new Dependency().with {
                    group = dependency.group ?: '.*'
                    name = dependency.name ?: '.*'
                    version = dependency.version ?: '.*'
                    rawExpiresDate = dependency.expires ?: ''

                    expires = dependency.expires ?
                        new Date().parse("yyyy-M-d", dependency.expires).time : Long.MAX_VALUE

                    return it
                })
            }
        }
    }

    /**
     * Status for a dependency respecting the whitelist
     */
    static enum Status {
        /**
         * Implies that the dependency is in the whitelist
         * The repository can use this dependency
         */
        AVAILABLE(false, false),
        /**
         * Implies that the dependency is not in the whitelist or the version
         * is not the correct one.
         * The repository shouldnt have this dependency, or this particular version.
         */
        INVALID(true, true),
        /**
         * Implies that the dependency is in the whitelist, but it has already expired
         * The repository should either remove this dependency, or update its expiry time
         * in the whitelist.
         */
        EXPIRED(true, true),
        /**
         * Implies that the dependency is in the whitelist, but it hasn't expire yet
         * The repository should known this and prepare to remove this dependency, or update its expiry time
         * in the whitelist.
         */
        GOING_TO_EXPIRE(true, false)

        private boolean shouldReport
        private boolean isBlocker

        Status(boolean shouldReport, boolean isBlocker) {
            this.shouldReport = shouldReport
            this.isBlocker = isBlocker
        }

        /**
         * If the status can be reported or not
         * @return boolean notifying if the status can be reported
         */
        boolean reportable() {
            return shouldReport
        }

        /**
         * If the status can be blocker or not
         * @return boolean notifying if the status can be blocker
         */
        boolean isBlocker() {
            return isBlocker
        }

        /**
         * Returns a formatted message for the dependency
         * @param dependency to format in the message
         * @return formatted message to print
         * @throws IllegalAccessException if trying to report a non reportable status
         */
        String message(String dependency) {
            if (!reportable()) {
                throw new IllegalAccessException('Cant report this type of dependency')
            }
            return "- ${dependency} (${name().toLowerCase().capitalize()})"
        }
    }

    /**
     * Whitelist Dependency DTO
     */
    static class Dependency {
        /**
         * groupId per pom definition
         */
        String group

        /**
         * name / artifactId per pom definition
         */
        String name

        /**
         * Version per pom definition
         */
        String version

        /**
         * Date time when the dependency becomes invalid. Until then, its considered as a valid dependency
         * Time is measured in milliseconds
         */
        long expires

        /**
         * Date time raw as got in the .json file.
         */
        String rawExpiresDate
    }
}