package com.mercadolibre.android.gradle.app.core.action.configurers

import com.mercadolibre.android.gradle.app.module.ModuleProvider.provideAppAndroidModules
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import org.gradle.api.Project

open class AppModuleConfigurer: ModuleConfigurer() {

    override fun configureProject(project: Project) {
        executeModuleConfig(provideAppAndroidModules(), project)
    }
}