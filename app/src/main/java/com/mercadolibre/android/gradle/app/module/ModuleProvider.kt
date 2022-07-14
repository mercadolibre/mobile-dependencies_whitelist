package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.AppLintModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.core.action.modules.plugin_description.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module

internal object ModuleProvider {

    private val androidAppModules =
        listOf<Module>(
            AppLintModule(),
            AppJacocoModule(),
            ApplicationLintOptionsModule(),
            AppPluginDescriptionModule()
        )

    fun provideAppAndroidModules(): List<Module> {
        return androidAppModules
    }
}