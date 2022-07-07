package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.ReleaseDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_TASK_FAIL_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.ExtensionProvider
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * The LintableModule module is in charge of configuring the Linteo in each of the variants of the project modules.
 */
class LintableModule : Module, ExtensionProvider, ExtensionGetter() {

    override fun getName(): String {
        return LINTABLE_EXTENSION
    }

    private val linters = listOf(
        LibraryAllowListDependenciesLint(),
        ReleaseDependenciesLint()
    )

    private val variants = arrayListOf<BaseVariant>()

    override fun configure(project: Project) {
        project.afterEvaluate {
            setUpLint(this)
        }
    }

    override fun createExtension(project: Project) {
        project.extensions.create(getName(), LintGradleExtension::class.java)
        for (subProject in project.subprojects) {
            subProject.extensions.create(getName(), LintGradleExtension::class.java)
        }
    }

    fun setUpLint(project: Project) {
        findExtension<LibraryExtension>(project)?.apply {
            libraryVariants.all { variants.add(this) }
        }
        findExtension<AppExtension>(project)?.apply {
            applicationVariants.all { variants.add(this) }
        }

        project.tasks.register(LINTABLE_TASK).configure {
            description = LINTABLE_DESCRIPTION
            doLast {
                findExtension<LintGradleExtension>(project)?.apply {
                    if (enabled) {
                        var buildErrored = false
                        for (linter in linters) {
                            val lintErrored = linter.lint(project, variants)
                            if (lintErrored) {
                                buildErrored = true
                            }
                        }
                        if (buildErrored) {
                            throw GradleException(LINT_TASK_FAIL_MESSAGE)
                        }
                    }
                }
            }
        }

        if (project.tasks.names.contains(LifecycleBasePlugin.CHECK_TASK_NAME)) {
            project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
                dependsOn(LINTABLE_TASK)
            }
        } else {
            project.tasks.configureEach {
                if (name == LifecycleBasePlugin.CHECK_TASK_NAME) {
                    dependsOn(LINTABLE_TASK)
                }
            }
        }
    }
}
