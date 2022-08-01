package com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.SEPARATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * The ListVariantsModule is responsible for providing the functionality to display the variants of all projects.
 */
class ListVariantsModule : Module() {

    /**
     * This is the method in charge of adding the task that shows the variants within the repository.
     */
    override fun configure(project: Project) {
        configureTask(project.tasks.register(LIST_VARIANTS_TASK).get(), project)
    }

    private fun configureTask(task: Task, project: Project) {
        with(task) {
            group = MELI_GROUP
            description = LIST_VARIANTS_DESCRIPTION
            doLast {
                printVariants(project)
            }
        }
    }

    /**
     * This is the method in charge of shows the variants within the repository.
     */
    fun printVariants(project: Project) {
        println("Root Project: ${project.name}".ansi(ANSI_GREEN))
        for (subProject in project.subprojects) {
            println(SEPARATOR)
            print("${subProject.name.ansi(ANSI_GREEN)} - ")
            findExtension<LibraryExtension>(subProject)?.apply {
                print("ModuleType: Library \n".ansi(ANSI_GREEN))
                for (variant in buildTypes) {
                    println("${variant.name.ansi(ANSI_YELLOW)}")
                }
            }

            findExtension<AppExtension>(subProject)?.apply {
                print("ModuleType: App \n".ansi(ANSI_GREEN))
                for (variant in buildTypes) {
                    println("${variant.name.ansi(ANSI_YELLOW)}")
                }
            }
        }
    }
}
