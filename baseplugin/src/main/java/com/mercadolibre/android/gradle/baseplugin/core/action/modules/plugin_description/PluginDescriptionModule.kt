package com.mercadolibre.android.gradle.baseplugin.core.action.modules.plugin_description

import com.mercadolibre.android.gradle.baseplugin.core.components.ALL_PLUGIN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MODULE_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

class PluginDescriptionModule: Module {

    override fun configure(project: Project) {
        project.tasks.register(PLUGIN_DESCRIPTION_TASK) {
            group = MELI_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            println(ALL_PLUGIN_DESCRIPTION)
            finalizedBy(PLUGIN_MODULES_DESCRIPTION_TASK)
        }

        project.tasks.register(PLUGIN_MODULES_DESCRIPTION_TASK) {
            group = MELI_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            println(MODULE_CONFIGURER_DESCRIPTION)
        }
    }
}