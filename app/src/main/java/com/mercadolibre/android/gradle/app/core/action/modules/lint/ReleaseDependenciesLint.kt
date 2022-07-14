package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_RELEASE_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL
import java.io.File
import java.util.stream.Stream
import org.gradle.api.Project

class ReleaseDependenciesLint: Lint() {

    private val ERROR_TITLE = "Error. Found non-release dependencies in the module release version:"

    private val FILE = "build/reports/${ReleaseDependenciesLint::class.java.simpleName}/${LINT_FILENAME}"

    override fun name(): String {
        return LINT_RELEASE_DEPENDENCIES_TASK
    }

    override fun lint(project: Project, variants: List<BaseVariant>): Boolean {
        findExtension<LintGradleExtension>(project)?.apply {
            if (!releaseDependenciesLintEnabled){
                return false
            }
        }

        val lintResultsFile = project.file(FILE)

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

    fun checkIsFailed(dependencies: Stream<String>, lintResultsFile: File): Boolean {
        for (dependency in dependencies) {
            println(dependency)

            if (!lintResultsFile.exists()) {
                lintResultsFile.parentFile.mkdirs()
                println(ERROR_TITLE)
                lintResultsFile.writeText(ERROR_TITLE)
            }

            lintResultsFile.appendText("${System.getProperty("line.separator")}${dependency}")
            println(dependency)
            return true
        }
        return false
    }

}