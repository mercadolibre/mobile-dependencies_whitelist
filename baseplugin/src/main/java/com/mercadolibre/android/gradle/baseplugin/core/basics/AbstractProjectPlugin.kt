package com.mercadolibre.android.gradle.baseplugin.core.basics

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.PluginWithConfigurers
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * AbstractPlugin is in charge of providing the functionality to a plugin that can be applied in a Project.
 */
abstract class AbstractProjectPlugin : Plugin<Project>, PluginWithConfigurers {
    /**
     * This method is in charge of requesting that a Project be configured.
     */
    override fun apply(project: Project) {
        for (configurer in configurers) {
            configurer.configureProject(project)
        }
    }
}
