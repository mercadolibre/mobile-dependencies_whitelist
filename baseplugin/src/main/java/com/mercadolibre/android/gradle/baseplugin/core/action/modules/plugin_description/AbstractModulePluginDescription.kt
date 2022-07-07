package com.mercadolibre.android.gradle.baseplugin.core.action.modules.plugin_description

import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * The AbstractModule Plugin Description class is in charge of providing the basic functionalities that the modules that describe a
 * Module Configurer must have.
 */
abstract class AbstractModulePluginDescription(val taskName: String, private val moduleName: String, val content: () -> String) : Module {

    override fun configure(project: Project) {
        if (!project.rootProject.tasks.names.contains(LIBRARY_PLUGIN_DESCRIPTION_TASK)) {
            project.rootProject.tasks.register(LIBRARY_PLUGIN_DESCRIPTION_TASK) { configureTask(this) }
            project.rootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK)?.finalizedBy(LIBRARY_PLUGIN_DESCRIPTION_TASK)
        }
    }

    fun configureTask(task: Task) {
        with(task) {
            group = MELI_GROUP
            description = PLUGIN_DESCRIPTION_DESCRIPTION

            doLast {
                printMessage(makeMessage(moduleName, content()))
            }
        }
    }

    fun printMessage(message: String) {
        println(message)
    }

    fun makeMessage(name: String, contentMessage: String): String {
        return "- $name\n$contentMessage"
    }
}
