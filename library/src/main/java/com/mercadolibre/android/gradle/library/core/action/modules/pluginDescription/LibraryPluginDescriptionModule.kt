package com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractPluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_MODULE_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider

/**
 * This module is responsible for showing the modules that are executed when implementing this plugin.
 */
internal class LibraryPluginDescriptionModule : AbstractPluginDescription(
    LIBRARY_MODULE_PLUGIN_DESCRIPTION_TASK,
    LibraryModuleConfigurer::class.java.simpleName.ansi(ANSI_YELLOW),
    "Library Module $ARROW ",
    { LibraryModuleConfigurer().getModules("Library Module", ModuleProvider.provideLibraryAndroidModules()) }
)
