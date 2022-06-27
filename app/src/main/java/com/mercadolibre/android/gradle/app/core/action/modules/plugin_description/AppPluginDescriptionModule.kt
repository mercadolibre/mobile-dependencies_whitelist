package com.mercadolibre.android.gradle.app.core.action.modules.plugin_description

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

class AppPluginDescriptionModule: Module {
    override fun configure(project: Project) {

        project.tasks.register(APP_PLUGIN_DESCRIPTION_TASK) {
            group = PLUGIN_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            print("""
                - ${AppModuleConfigurer::class.java.simpleName.ansi(ANSI_YELLOW)}
                ${AppModuleConfigurer().getModules("App Modules", ModuleProvider.provideAppAndroidModules())}
            """.trimIndent())
        }

        project.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK)?.dependsOn(APP_PLUGIN_DESCRIPTION_TASK)
    }
}