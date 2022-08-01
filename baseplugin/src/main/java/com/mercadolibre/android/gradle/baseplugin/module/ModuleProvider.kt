package com.mercadolibre.android.gradle.baseplugin.module

import com.mercadolibre.android.gradle.baseplugin.core.components.JAVA_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.components.PROJECT_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.components.SETTINGS_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule

/**
 * ModuleProvider is in charge of providing the modules that the plugin will add to the projects, modules and settings.
 */
internal object ModuleProvider {

    /**
     * This method is responsible for providing the list of java modules.
     */
    fun provideJavaModules(): List<Module> = JAVA_MODULES

    /**
     * This method is responsible for providing the list of project modules.
     */
    fun provideProjectModules(): List<Module> = PROJECT_MODULES

    /**
     * This method is responsible for providing the list of settings modules.
     */
    fun provideSettingsModules(): List<SettingsModule> = SETTINGS_MODULES

    /**
     * This method is responsible for providing all modules.
     */
    fun provideAllModules(): List<Module> = arrayListOf<Module>().apply {
        addAll(JAVA_MODULES)
        addAll(PROJECT_MODULES)
    }.toList()
}
