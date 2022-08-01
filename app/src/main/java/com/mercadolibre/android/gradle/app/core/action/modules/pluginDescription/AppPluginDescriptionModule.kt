package com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractPluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_MODULE_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi

/**
 * This module is responsible for showing the modules that are executed when implementing this plugin.
 */
class AppPluginDescriptionModule : AbstractPluginDescription(
    APP_MODULE_PLUGIN_DESCRIPTION_TASK,
    AppModuleConfigurer::class.java.simpleName.ansi(ANSI_YELLOW),
    "App Modules $ARROW ",
    { AppModuleConfigurer().getModules("App Modules", ModuleProvider.provideAppAndroidModules()) }
)
