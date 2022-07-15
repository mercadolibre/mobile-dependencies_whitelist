package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_RELEASE_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_RELEASE_FILE
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL
import org.gradle.api.Project
import java.io.File
import java.util.stream.Stream

/**
 * The ReleaseDependenciesLint class is in charge of reviewing all the dependencies of the project to report if there is any deprecated
 * in a Release App.
 */
class ReleaseDependenciesLint : Lint() {

    /**
     * This method is responsible for providing a name to the linteo class.
     */
    override fun name(): String = LINT_RELEASE_DEPENDENCIES_TASK

    /**
     * This method is responsible for verifying that the dependencies of all the variants are valid or
     * if they are about to expire, perform the warnign.
     */
    override fun lint(project: Project, variants: List<BaseVariant>): Boolean {
        findExtension<LintGradleExtension>(project)?.apply {
            if (!releaseDependenciesLintEnabled) {
                return false
            }
        }

        val lintResultsFile = project.file(LINT_RELEASE_FILE)

        if (lintResultsFile.exists()) {
            lintResultsFile.delete()
        }

        val dependencies =
            project.configurations
                .stream()
                .map { config -> config.dependencies }
                .flatMap { dependencies -> dependencies.stream() }
                .filter { dependency ->
                    dependency.version?.contains(PUBLISHING_EXPERIMENTAL) == true || dependency.version?.contains(PUBLISHING_LOCAL) == true
                }
                .map { dependency -> "${dependency.group}:${dependency.name}:${dependency.version}" }
                .distinct()

        return checkIsFailed(dependencies, lintResultsFile)
    }

    /**
     * This method is in charge of verifying if the Lint failed.
     */
    fun checkIsFailed(dependencies: Stream<String>, lintResultsFile: File): Boolean {
        for (dependency in dependencies) {
            if (!lintResultsFile.exists()) {
                lintResultsFile.parentFile.mkdirs()
                println(LINT_RELEASE_FILE)
                lintResultsFile.writeText(LINT_RELEASE_FILE)
            }

            lintResultsFile.appendText("${System.getProperty("line.separator")}$dependency")
            println(dependency)
            return true
        }
        return false
    }
}
