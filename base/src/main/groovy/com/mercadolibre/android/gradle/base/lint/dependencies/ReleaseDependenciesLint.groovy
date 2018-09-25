package com.mercadolibre.android.gradle.base.lint.dependencies

import com.mercadolibre.android.gradle.base.BasePlugin
import com.mercadolibre.android.gradle.base.lint.Lint
import org.gradle.api.Project

/**
 * Class that lints the dependencies in the project checking that it only
 * compiles the whitelisted ones
 *
 * Author: Santi Aguilera
 */
class ReleaseDependenciesLint implements Lint {

    private static final String ERROR_TITLE = "Error. Found non-release dependencies in the module release version:"

    private static final String FILE = "build/reports/${ReleaseDependenciesLint.class.simpleName}/${Lint.LINT_FILENAME}"

    /**
     * Checks the dependencies the project contains are all release or alpha.
     *
     * This is only ran if the code is going to be going to master/release-^/X
     * 
     * This throws GradleException if errors are found.
     */
    boolean lint(Project project, def variants) {
        if (!project.lintGradle.releaseDependenciesLintEnabled) {
            return false
        }

        boolean hasFailed
        File lintResultsFile = project.file(FILE)

        // This is a new run, so remove the file if it exists, we will override it
        if (lintResultsFile.exists()) {
            lintResultsFile.delete()
        }

        if (project.plugins.hasPlugin(BasePlugin.ANDROID_APPLICATION_PLUGIN) ||
                project.plugins.hasPlugin(BasePlugin.ANDROID_LIBRARY_PLUGIN)) {
            project.configurations
                    .stream()
                    .map { config -> config.dependencies }
                    .flatMap { dependencies -> dependencies.stream() }
                    .filter { dependency -> dependency.version?.contains("EXPERIMENTAL") ||
                                        dependency.version?.contains("LOCAL") }
                    .map { dependency -> "${dependency.group}:${dependency.name}:${dependency.version}" }
                    .distinct()
                    .forEach { dependency ->
                        hasFailed = true

                        if (!lintResultsFile.exists()) {
                            lintResultsFile.parentFile.mkdirs()
                            println ERROR_TITLE
                            lintResultsFile << ERROR_TITLE
                        }

                        lintResultsFile.append("${System.getProperty("line.separator")}${dependency}")
                        println dependency
                    }
        }

        return hasFailed
    }

    /**
     * Returns the task name
     */
    String name() {
        return "lintReleaseDependencies"
    }

}
