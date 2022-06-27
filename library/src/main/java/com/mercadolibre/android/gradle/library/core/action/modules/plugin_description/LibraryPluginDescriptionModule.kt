package com.mercadolibre.android.gradle.library.core.action.modules.plugin_description

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import org.gradle.api.Project

class LibraryPluginDescriptionModule: Module {
    override fun configure(project: Project) {

        project.tasks.register(LIBRARY_PLUGIN_DESCRIPTION_TASK) {
            group = PLUGIN_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            print("""
                - ${LibraryModuleConfigurer::class.java.simpleName.ansi(ANSI_YELLOW)}
                ${LibraryModuleConfigurer().getModules("Library Module", ModuleProvider.provideLibraryAndroidModules())}
            """.trimIndent())
        }

        project.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK)?.dependsOn(APP_PLUGIN_DESCRIPTION_TASK)
    }
}