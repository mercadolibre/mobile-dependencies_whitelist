package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_TASK_FAIL_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * The LintableModule module is in charge of configuring the Linteo in each of the variants of the project modules.
 */
abstract class LintableModule : Module, ExtensionGetter() {

    abstract fun getVariants(project: Project): List<BaseVariant>
    abstract fun getLinter(): Lint

    /**
     * This is the method in charge of executing the lint within a project.
     */
    override fun configure(project: Project) {
        project.afterEvaluate {
            setUpLint(this)
        }
    }

    /**
     * This is the method in charge of configuring the linteo, whether it is an app or a library,
     * and verifying that all the dependencies are correct.
     */
    fun setUpLint(project: Project) {
        project.tasks.register(LINTABLE_TASK).configure {
            description = LINTABLE_DESCRIPTION
            doLast {
                configureVariants(project)
            }
        }

        project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
            dependsOn(LINTABLE_TASK)
        }
    }

    /**
     * This is the method in charge of verifying that all the dependencies are correct.
     */
    open fun configureVariants(project: Project) {
        findExtension<LintGradleExtension>(project)?.apply {
            if (enabled) {
                val lintErrored = getLinter().lint(project, getVariants(project))
                if (lintErrored) {
                    throw GradleException(LINT_TASK_FAIL_MESSAGE)
                }
            }
        }
    }
}
