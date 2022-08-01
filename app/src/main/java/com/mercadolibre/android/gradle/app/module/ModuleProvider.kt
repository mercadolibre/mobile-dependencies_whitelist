package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.app.core.action.modules.bugsnag.BugsnagModule
import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.AppLintModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription.AppPluginDescriptionExtensionsModule
import com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module

/**
 * ModuleProvider is in charge of providing the modules that the plugin will add to apps modules.
 */
internal object ModuleProvider {

    private val androidAppModules =
        listOf(
            AppLintModule(),
            AppJacocoModule(),
            ApplicationLintOptionsModule(),
            AppPluginDescriptionModule(),
            BugsnagModule(),
            AppPluginDescriptionExtensionsModule()
        )

    /**
     * This method is responsible for providing the list of android modules.
     */
    fun provideAppAndroidModules(): List<Module> = androidAppModules
}
