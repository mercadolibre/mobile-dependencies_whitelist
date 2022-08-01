package com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.app.core.action.configurers.AppExtensionConfigurer
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractPluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_EXTENSION_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi

/**
 * This module is responsible for showing the modules that are executed when implementing this plugin.
 */
class AppPluginDescriptionExtensionsModule : AbstractPluginDescription(
    APP_EXTENSION_PLUGIN_DESCRIPTION_TASK,
    AppExtensionConfigurer::class.java.simpleName.ansi(ANSI_YELLOW),
    "App Extensions $ARROW ",
    {
        var names = ""
        for (extensionProvider in ModuleProvider.provideAppAndroidModules()) {
            names += "${extensionProvider.getExtensionName()}, "
        }
        if (names.length > 3) {
            names.substring(0, names.length - 2).ansi(ANSI_GREEN)
        } else {
            names
        }
    }
)
