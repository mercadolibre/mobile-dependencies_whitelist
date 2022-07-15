package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.app.core.action.components.ANDROID_APPLICATION_MODULES
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module

/**
 * ModuleProvider is in charge of providing the modules that the plugin will add to apps modules.
 */
internal object ModuleProvider {
    /**
     * This method is responsible for providing the list of android modules.
     */
    fun provideAppAndroidModules(): List<Module> = ANDROID_APPLICATION_MODULES
}
