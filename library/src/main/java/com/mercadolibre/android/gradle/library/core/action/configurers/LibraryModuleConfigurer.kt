package com.mercadolibre.android.gradle.library.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider.provideLibraryAndroidModules
import org.gradle.api.Project

open class LibraryModuleConfigurer: ModuleConfigurer() {

    override fun configureProject(project: Project) {
        executeModuleConfig(provideLibraryAndroidModules(), project)
    }

}