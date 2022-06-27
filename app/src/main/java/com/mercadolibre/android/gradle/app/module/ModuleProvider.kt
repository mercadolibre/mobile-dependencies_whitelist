package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.app.core.action.components.ANDROID_APPLICATION_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module

internal object ModuleProvider {

    fun provideAppAndroidModules(): List<Module> {
        return ANDROID_APPLICATION_MODULES
    }
}