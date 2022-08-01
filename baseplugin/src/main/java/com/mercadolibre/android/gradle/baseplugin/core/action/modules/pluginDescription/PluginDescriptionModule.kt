package com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.components.ALL_PLUGIN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_SUB_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MODULE_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

/**
 * PluginDescriptionModule is in charge of providing the functionality of showing the description of what the plugin does.
 */
internal class PluginDescriptionModule : Module() {

    /**
     * This method is in charge of generating the tasks that will describe the plugins.
     */
    override fun configure(project: Project) {
        val moduleDescriptionTask = project.tasks.register(PLUGIN_MODULES_DESCRIPTION_TASK).get()

        with(moduleDescriptionTask) {
            group = MELI_SUB_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            doLast {
                println(MODULE_CONFIGURER_DESCRIPTION)
            }
        }

        val pluginDescriptionTask = project.tasks.register(PLUGIN_DESCRIPTION_TASK).get()

        with(pluginDescriptionTask) {
            group = MELI_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            finalizedBy(project.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK))

            doLast {
                println(ALL_PLUGIN_DESCRIPTION)
            }

            finalizedBy(PLUGIN_MODULES_DESCRIPTION_TASK)
        }
    }
}
