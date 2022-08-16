package com.mercadolibre.android.gradle.baseplugin.core.basics

import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * AbstractPlugin is in charge of providing the functionality to a plugin that can be applied in a Settings.
 */
abstract class AbstractSettingsPlugin : Plugin<Settings> {
    /**
     * This method is in charge of requesting that a Project be configured.
     */
    override fun apply(settings: Settings) {
        for (settingsModule in ModuleProvider.provideSettingsModules()) {
            settingsModule.configure(settings)
        }
    }
}
