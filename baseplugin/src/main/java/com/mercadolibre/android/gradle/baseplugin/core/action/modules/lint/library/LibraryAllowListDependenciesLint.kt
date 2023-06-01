package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_BLOCKER
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_WARNING
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import com.mercadolibre.android.gradle.baseplugin.core.extensions.isLocal
import com.mercadolibre.android.gradle.baseplugin.core.extensions.matches
import com.mercadolibre.android.gradle.baseplugin.core.extensions.new
import com.mercadolibre.android.gradle.baseplugin.core.extensions.parseAllowlistDefaults
import com.mercadolibre.android.gradle.baseplugin.core.extensions.parseAvailable
import com.mercadolibre.android.gradle.baseplugin.core.extensions.parseProjectDefaults
import com.mercadolibre.android.gradle.baseplugin.core.extensions.setup
import com.mercadolibre.android.gradle.baseplugin.core.usecase.GetAllowedDependenciesUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.LogLibraryBlockersComplianceUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.LogLibraryBlockersUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.LogLibraryWarningsComplianceUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.LogLibraryWarningsUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.ValidateAlphaUseCase
import com.mercadolibre.android.gradle.baseplugin.core.usecase.ValidateDependencyStatusUseCase.validate
import com.mercadolibre.android.gradle.baseplugin.core.usecase.ValidateUnlimitedDeadlineUseCase
import org.gradle.api.Project

private typealias InvalidBuffer = () -> Unit

private typealias ToExpireBuffer = () -> Unit

private const val UNSPECIFIED_GRADLE_VERSION = "unspecified"

/**
 * The LibraryAllowListDependenciesLint class is in charge of reviewing all the dependencies of the project through the AllowList to
 * report if there is any deprecated in a Library.
 */
class LibraryAllowListDependenciesLint(
    private val variantNames: List<String>
) : Lint() {

    /** This list contains the dependencies that are about to expire. */
    private val allowListGoingToExpireBuffer = mutableListOf<ToExpireBuffer>()

    private val allowListInvalidBuffer = mutableListOf<InvalidBuffer>()

    /**
     * This method is responsible for providing a name to the linteo class.
     */
    override fun name(): String = LINT_DEPENDENCIES_TASK

    private lateinit var project: Project

    private val lintGradle by lazy { setup(project) }

    /**
     * This method is responsible for verifying that the dependencies of all the variants are valid or
     * if they are about to expire, perform the warning.
     */
    override fun lint(project: Project): Boolean {
        this.project = project
        lintGradle.apply {
            if (dependenciesLintEnabled) {
                variantNames.forEach { name ->
                    analyzeByVariantName(name)
                }

                allowListInvalidBuffer.apply {
                    if (isNotEmpty()) {
                        val file = project.file(LINT_LIBRARY_FILE_BLOCKER).new()
                        val messages = { forEach { log -> log() } }
                        LogLibraryBlockersComplianceUseCase.log(file, dependencyAllowListUrl, messages)
                    }
                }

                allowListGoingToExpireBuffer.apply {
                    if (isNotEmpty()) {
                        val file = project.file(LINT_LIBRARY_FILE_WARNING).new()
                        val messages = { forEach { log -> log() } }
                        LogLibraryWarningsComplianceUseCase.log(file, messages)
                    }
                }
            }
        }

        return allowListInvalidBuffer.isNotEmpty()
    }

    private fun analyzeByVariantName(name: String) {
        project.configurations.findByName(name)?.apply {
            for (rawProjectDependencies in dependencies) {
                val projectDependency = rawProjectDependencies.parseProjectDefaults()
                analyzeOrNull(projectDependency)?.let { analyzed ->
                    validate(analyzed).apply {
                        if (isBlocker) {
                            addToInvalidBuffer(analyzed.copy(status = this))
                        } else if (shouldReport) {
                            addToWarningBuffer(analyzed.copy(status = this))
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is responsible for verifying if the dependency has to be reported, or has any warning.
     */
    private fun analyzeOrNull(projectDependency: Dependency): DependencyAnalysis? {
        val isNotInvalidAnalysis = !projectDependency.fullName()
            .contains(UNSPECIFIED_GRADLE_VERSION) &&
            !projectDependency.isLocal(project)

        return if (isNotInvalidAnalysis) {
            analyzeByDependency(projectDependency)
        } else {
            null
        }
    }

    private fun analyzeByDependency(projectDependency: Dependency): DependencyAnalysis {
        var analysis = DependencyAnalysis(projectDependency = projectDependency)
        for (
            rawAllowListDependency
            in GetAllowedDependenciesUseCase.get(lintGradle.dependencyAllowListUrl)
        ) {
            rawAllowListDependency.parseAllowlistDefaults().let { allowListDependency ->
                if (projectDependency.matches(allowListDependency)) {
                    val isAllowedAlpha = ValidateAlphaUseCase.validate(
                        allowListDependency,
                        project,
                        lintGradle
                    )
                    analysis = analysis.copy(
                        allowListDependency = allowListDependency,
                        isAllowedAlpha = isAllowedAlpha
                    )
                }

                val isUnlimitedAvailableVersion = ValidateUnlimitedDeadlineUseCase.validate(
                    projectDependency,
                    allowListDependency
                )

                if (isUnlimitedAvailableVersion) {
                    analysis = analysis.copy(availableVersion = allowListDependency.parseAvailable())
                }
            }
        }
        return analysis
    }

    private fun addToInvalidBuffer(analyzed: DependencyAnalysis) {
        allowListInvalidBuffer.add {
            LogLibraryBlockersUseCase.log(
                project.file(LINT_LIBRARY_FILE_BLOCKER),
                analyzed,
            )
        }
    }

    private fun addToWarningBuffer(analyzed: DependencyAnalysis) {
        allowListGoingToExpireBuffer.add {
            LogLibraryWarningsUseCase.log(
                project.file(LINT_LIBRARY_FILE_WARNING),
                analyzed
            )
        }
    }
}
