package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import org.gradle.api.Project

/**
 * The Plugin Configurer is in charge of adding the necessary plugins so that the modules contain the necessary functionality
 * to be an App or a Library.
 */
open class PluginConfigurer(private val plugins: List<String>) : Configurer {

    override fun getDescription(): String {
        return PLUGIN_DESCRIPTION_DESCRIPTION
    }

    override fun configureProject(project: Project) {
        for (plugin in plugins) {
            addPlugin(project, plugin)
        }
    }

    private fun addPlugin(project: Project, pluginId: String) {
        if (!project.plugins.hasPlugin(pluginId)) {
            project.plugins.apply(pluginId)
        }
    }
}
