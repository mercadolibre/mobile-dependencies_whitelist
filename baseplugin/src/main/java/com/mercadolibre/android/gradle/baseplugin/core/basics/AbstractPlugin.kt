package com.mercadolibre.android.gradle.baseplugin.core.basics

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.PluginWithConfigurers
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

/**
 * AbstractPlugin is in charge of providing the functionality to a plugin that can be applied in a Project or in a Settings.
 */
abstract class AbstractPlugin : Plugin<Any>, PluginWithConfigurers {

    /**
     * This method is responsible for applying the plugin either in a Settings or in Project.
     */
    override fun apply(target: Any) {
        when (target) {
            is Settings -> applySettings(target)
            is Project -> applyBasePlugin(target)
        }
    }

    /**
     * This method is responsible for requesting that a Settings be configured.
     */
    fun applySettings(settings: Settings) {
        for (settingsModule in ModuleProvider.provideSettingsModules()) {
            settingsModule.configure(settings)
        }
    }

    /**
     * This method is in charge of requesting that a Project be configured.
     */
    fun applyBasePlugin(project: Project) {
        for (configurer in configurers) {
            configurer.configureProject(project)
        }
    }
}
