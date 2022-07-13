package com.mercadolibre.android.gradle.library.module

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.core.action.modules.lint.LibraryLintModule
import com.mercadolibre.android.gradle.library.core.action.modules.plugin_description.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.core.action.modules.testeable.LibraryTestableModule

internal object ModuleProvider {

    private val androidLibraryModules =
        listOf<Module>(
            LibraryLintModule(),
            LibraryJacocoModule(),
            LibraryTestableModule(),
            LibraryPublishableModule(),
            LibraryPluginDescriptionModule()
        )

    fun provideLibraryAndroidModules(): List<Module> {
        return androidLibraryModules
    }

}