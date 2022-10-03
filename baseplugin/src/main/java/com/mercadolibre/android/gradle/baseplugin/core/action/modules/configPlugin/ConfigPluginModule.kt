package com.mercadolibre.android.gradle.baseplugin.core.action.modules.configPlugin

import com.mercadolibre.android.gradle.baseplugin.core.action.providers.OutPutUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.DF_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * This module is responsible for generating the tasks that will add the gradle plugin to the modules that have it.
 */
class ConfigPluginModule : Module() {

    private val taskName = "lintMeliPlugins"

    private val LIBRARY_PLUGIN_NAME = "library"
    private val APP_PLUGIN_NAME = "app"
    private val DF_PLUGIN_NAME = "dynamicfeatures"

    private val PLUGIN_BASE_ID = "mercadolibre.gradle.config."

    private val PLUGIN_LINT_TASK_FAIL_MESSAGE =
        "Errors found while running plugin lints, please check the console output for more information"

    /** This variable is responsible for verify if is needed a warning or a error. */
    var isBlocker = false

    /**
     * This method is responsible for call all modules.
     */
    override fun configure(project: Project) {
        for (subProject in project.subprojects) {
            subProject.afterEvaluate {
                createTask(subProject)
            }
        }
    }

    /**
     * This method is responsible to create the task to lint Meli Plugins.
     */
    fun createTask(subProject: Project) {
        val task = subProject.tasks.register(taskName).get()
        task.group = MELI_GROUP
        task.doLast {
            lint(subProject)
        }

        subProject.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
            dependsOn(taskName)
        }
    }

    private fun containsPlugin(project: Project, plugin: String) = project.plugins.findPlugin(plugin) != null

    private fun hasAnyMeliPlugin(project: Project): Boolean {
        for (pluginName in listOf(LIBRARY_PLUGIN_NAME, APP_PLUGIN_NAME, DF_PLUGIN_NAME)) {
            if (containsPlugin(project, "$PLUGIN_BASE_ID$pluginName")) {
                return true
            }
        }
        return false
    }

    private fun witchPluginHas(project: Project): String? {
        return when {
            containsPlugin(project, LIBRARY_PLUGIN) -> LIBRARY_PLUGIN_NAME
            containsPlugin(project, APP_PLUGIN) -> APP_PLUGIN_NAME
            containsPlugin(project, DF_PLUGIN) -> DF_PLUGIN_NAME
            else -> null
        }
    }

    /**
     * This method is responsible for verifying which modules do not have the gradle plugin and generating the tasks to add it.
     */
    fun lint(subProject: Project) {
        if (!hasAnyMeliPlugin(subProject)) {
            val type = witchPluginHas(subProject)

            if (type != null) {

                val message = listOf(
                    "${subProject.name} needs Meli ${type.capitalize()} Plugin",
                    "You can use the migration guide to proceed: " +
                        "https://sites.google.com/mercadolibre.com/mobile/gu%C3%ADas-y-problemas/migracion-plugin-de-gradle"
                )

                if (isBlocker) {
                    OutPutUtils.sendMultipleErrorMessages(message)
                    throw GradleException(PLUGIN_LINT_TASK_FAIL_MESSAGE)
                } else {
                    OutPutUtils.sendMultipleWarningMessages(message)
                }
            }
        }
    }
}
