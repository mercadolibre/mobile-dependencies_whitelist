package com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.ALL_PLUGIN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_SUB_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MODULE_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * PluginDescriptionModule is in charge of providing the functionality of showing the description of what the plugin does.
 */
internal class PluginDescriptionModule : Module() {

    /**
     * This method is in charge of generating the tasks that will describe the plugins.
     */
    override fun configure(project: Project) {
        configureModuleDescriptionTask(createModuleDescriptionTask(project))
        configurePluginDescriptionTask(createPluginDescriptionTask(project))
    }

    private fun createPluginDescriptionTask(project: Project): Task {
        return project.tasks.register(PLUGIN_DESCRIPTION_TASK).get()
    }

    private fun createModuleDescriptionTask(project: Project): Task {
        return project.tasks.register(PLUGIN_MODULES_DESCRIPTION_TASK).get()
    }

    private fun configurePluginDescriptionTask(pluginDescriptionTask: Task) {
        with(pluginDescriptionTask) {
            group = MELI_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            doLast {
                OutputUtils.logMessage(ALL_PLUGIN_DESCRIPTION)
            }

            finalizedBy(PLUGIN_MODULES_DESCRIPTION_TASK)
        }
    }

    private fun configureModuleDescriptionTask(moduleDescriptionTask: Task) {
        with(moduleDescriptionTask) {
            group = MELI_SUB_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            doLast {
                OutputUtils.logMessage(MODULE_CONFIGURER_DESCRIPTION)
            }
        }
    }
}
