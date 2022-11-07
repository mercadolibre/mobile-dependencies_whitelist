package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_TASK_FAIL_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.WARNIGN_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * The LintableModule module is in charge of configuring the Linteo in each of the variants of the project modules.
 */
abstract class LintableModule(private val taskName: String, private val taskDescription: String) : Module() {

    /**
     * This method is responsible for execute the configuration of the module.
     */
    override fun executeModule(project: Project) {
        val extension = findExtension(project, getExtensionName()) as? LintGradleExtension
        if (extension != null) {
            if (extension.dependenciesLintEnabled && extension.releaseDependenciesLintEnabled && extension.pluginsLintEnabled) {
                configure(project)
            } else {
                println("$WARNIGN_MESSAGE The ${getExtensionName()} is manually disabled in ${project.name} module.")
            }
        } else {
            configure(project)
        }
    }

    /**
     * This method is responsible for providing the extension name that Lintable needs to work.
     */
    override fun createExtension(project: Project) {
        if (project.extensions.findByName(LINTABLE_EXTENSION) == null) {
            project.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)
        }
    }

    /**
     * This method is responsible for providing the extension name that Lintable needs.
     */
    override fun getExtensionName(): String = LINTABLE_EXTENSION

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    abstract fun getLinter(project: Project): Lint

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
        project.tasks.register(taskName).configure {
            description = taskDescription
            group = MELI_GROUP
            doLast {
                configureVariants(project)
            }
        }

        project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
            dependsOn(taskName)
        }
    }

    /**
     * This is the method in charge of verifying that all the dependencies are correct.
     */
    open fun configureVariants(project: Project) {
        val lintErrored = getLinter(project).lint(project)
        if (lintErrored) {
            throw GradleException(LINT_TASK_FAIL_MESSAGE)
        }
    }
}
