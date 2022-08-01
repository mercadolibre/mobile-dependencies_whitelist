package com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractPluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_EXTENSION_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryExtensionConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider

/**
 * This module is responsible for showing the modules that are executed when implementing this plugin.
 */
class LibraryPluginDescriptionExtensionsModule : AbstractPluginDescription(
    LIBRARY_EXTENSION_PLUGIN_DESCRIPTION_TASK,
    LibraryExtensionConfigurer::class.java.simpleName.ansi(ANSI_YELLOW),
    "Library Extensions $ARROW ",
    {
        var names = ""
        for (extensionProvider in ModuleProvider.provideLibraryAndroidModules()) {
            names += "${extensionProvider.getExtensionName()}, "
        }
        if (names.length > 3) {
            names.substring(0, names.length - 2).ansi(ANSI_GREEN)
        } else {
            names
        }
    }
)
