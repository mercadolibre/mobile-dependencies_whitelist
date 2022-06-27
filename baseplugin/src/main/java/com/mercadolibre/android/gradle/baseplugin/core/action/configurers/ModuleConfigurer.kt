package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.MODULE_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

open class ModuleConfigurer: Configurer {

    override fun getDescription(): String {
        return MODULE_CONFIGURER_DESCRIPTION
    }

    open fun getModules(name: String, modules: List<Any>): String {
        var listOfModules = ""
        listOfModules += "${name.ansi(ANSI_YELLOW)}\n"
        for (module in modules){
            listOfModules += "\t\t- ${module::class.java.simpleName.ansi(ANSI_GREEN)}\n"
        }
        return listOfModules
    }

    override fun configureProject(project: Project){
        with(ModuleProvider){
            project.plugins.withType(JavaPlugin::class.java) {
                executeModuleConfig(provideJavaModules(), project)
            }
            for (subProject in project.subprojects) {
                subProject.plugins.withType(JavaPlugin::class.java) {
                    executeModuleConfig(provideJavaModules(), subProject)
                }
            }

            executeModuleConfig(provideProjectModules(), project)
        }
    }

    fun executeModuleConfig(modules: List<Module>, project: Project){
        for (module in modules) {
            module.configure(project)
        }
    }
}