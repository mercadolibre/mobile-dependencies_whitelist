package com.mercadolibre.android.gradle.library.core.action.modules.plugin_description

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.plugin_description.AbstractModulePluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider

/**
 * This module is responsible for showing the modules that are executed when implementing this plugin.
 */
internal class LibraryPluginDescriptionModule : AbstractModulePluginDescription(
    LIBRARY_PLUGIN_DESCRIPTION_TASK,
    LibraryModuleConfigurer::class.java.simpleName.ansi(ANSI_YELLOW),
    { LibraryModuleConfigurer().getModules("Library Module", ModuleProvider.provideLibraryAndroidModules()) }
)
