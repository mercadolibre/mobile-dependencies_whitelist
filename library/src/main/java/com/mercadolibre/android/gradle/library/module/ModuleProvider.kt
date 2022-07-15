package com.mercadolibre.android.gradle.library.module

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.core.action.modules.lint.LibraryLintModule
import com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.core.action.modules.testeable.LibraryTestableModule

/**
 * ModuleProvider is in charge of providing the modules that the plugin will add to libraries modules.
 */
internal object ModuleProvider {

    private val androidLibraryModules =
        listOf<Module>(
            LibraryLintModule(),
            LibraryJacocoModule(),
            LibraryTestableModule(),
            LibraryPublishableModule(),
            LibraryPluginDescriptionModule()
        )

    /**
     * This method is responsible for providing the list of library modules.
     */
    fun provideLibraryAndroidModules(): List<Module> = androidLibraryModules
}
