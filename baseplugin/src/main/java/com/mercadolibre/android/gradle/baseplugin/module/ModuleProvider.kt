package com.mercadolibre.android.gradle.baseplugin.module

import com.mercadolibre.android.gradle.baseplugin.core.components.JAVA_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.components.PROJECT_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.components.SETTINGS_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule

internal object ModuleProvider {

    fun provideJavaModules(): List<Module> {
        return JAVA_MODULES
    }

    fun provideProjectModules(): List<Module> {
        return PROJECT_MODULES
    }

    fun provideSettingsModules(): List<SettingsModule> {
        return SETTINGS_MODULES
    }
    
}