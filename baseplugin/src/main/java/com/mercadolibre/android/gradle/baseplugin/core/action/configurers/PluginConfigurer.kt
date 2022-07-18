package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import org.gradle.api.Project

/**
 * The Plugin Configurer is in charge of adding the necessary plugins so that the modules contain the necessary functionality
 * to be an App or a Library.
 */
open class PluginConfigurer(private val plugins: List<String>) : Configurer {

    /**
     * This method allows us to get a description of what this Configurer does.
     */
    override fun getDescription(): String = PLUGIN_DESCRIPTION_DESCRIPTION

    /**
     * This method is responsible for applying all the necessary plugins to the project.
     */
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
