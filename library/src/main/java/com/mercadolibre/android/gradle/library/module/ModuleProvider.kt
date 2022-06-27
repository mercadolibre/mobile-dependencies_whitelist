package com.mercadolibre.android.gradle.library.module

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.library.core.action.components.ANDROID_LIBRARY_MODULES

internal object ModuleProvider {

    fun provideLibraryAndroidModules(): List<Module> {
        return ANDROID_LIBRARY_MODULES
    }

}