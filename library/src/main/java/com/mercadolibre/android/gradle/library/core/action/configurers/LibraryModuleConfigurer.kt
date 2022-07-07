package com.mercadolibre.android.gradle.library.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider.provideLibraryAndroidModules
import org.gradle.api.Project

/**
 * The Library Module Configurer is in charge of requesting all modules to add their functionality to the module they are pointing to.
 */
open class LibraryModuleConfigurer : ModuleConfigurer() {
    override fun configureProject(project: Project) {
        executeModuleConfig(provideLibraryAndroidModules(), project)
    }
}
