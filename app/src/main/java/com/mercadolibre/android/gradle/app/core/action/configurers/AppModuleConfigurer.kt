package com.mercadolibre.android.gradle.app.core.action.configurers

import com.mercadolibre.android.gradle.app.module.ModuleProvider.provideAppAndroidModules
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import org.gradle.api.Project

/**
 * The App Module Configurer is in charge of requesting all modules to add their functionality to the module they are pointing to.
 */
open class AppModuleConfigurer : ModuleConfigurer() {
    /**
     * This method asks all modules to configure the project where the plugin was applied.
     */
    override fun configureProject(project: Project) {
        executeModuleConfig(provideAppAndroidModules(), project)
    }
}
