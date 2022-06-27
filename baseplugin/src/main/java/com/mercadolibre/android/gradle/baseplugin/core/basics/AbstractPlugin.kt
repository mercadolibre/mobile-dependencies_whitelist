package com.mercadolibre.android.gradle.baseplugin.core.basics

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.PluginWithConfigurers
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

abstract class AbstractPlugin: Plugin<Any>, PluginWithConfigurers {

    override fun apply(target: Any) {
        when (target) {
            is Settings -> applySettings(target)
            is Project -> applyBasePlugin(target)
        }
    }

    fun applySettings(settings: Settings) {
        for (settingsModule in ModuleProvider.provideSettingsModules()) {
            settingsModule.configure(settings)
        }
    }

    fun applyBasePlugin(project: Project){
        for (configurer in configurers) {
            configurer.configureProject(project)
        }
    }
}