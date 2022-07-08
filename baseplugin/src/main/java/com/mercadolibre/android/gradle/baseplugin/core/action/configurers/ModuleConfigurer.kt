package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.MODULE_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * The Module Configurer is in charge of requesting all modules to add their functionality to the module they are pointing to.
 */
open class ModuleConfigurer : Configurer {

    /**
     * This method allows us to get a description of what this Configurer does.
     */
    override fun getDescription(): String {
        return MODULE_CONFIGURER_DESCRIPTION
    }

    /**
     * This method is in charge of listing the modules that are applied to the project to which it is being configured.
     */
    fun getModules(name: String, modules: List<Any>): String {
        var listOfModules = "${name.ansi(ANSI_YELLOW)} $ARROW "
        for (module in modules) {
            listOfModules += "${module::class.java.simpleName.ansi(ANSI_GREEN)}, "
        }
        return listOfModules.substring(0, listOfModules.length - 2)
    }

    /**
     * This method is responsible for requesting the corresponding modules to add their functionality to the project.
     */
    override fun configureProject(project: Project) {
        with(ModuleProvider) {
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

    private fun executeModuleConfig(modules: List<Module>, project: Project) {
        for (module in modules) {
            module.configure(project)
        }
    }
}
