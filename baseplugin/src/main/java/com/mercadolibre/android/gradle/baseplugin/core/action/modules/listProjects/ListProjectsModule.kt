package com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects

import com.mercadolibre.android.gradle.baseplugin.core.components.BEGINNING_TOKEN
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_PROJECTS_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_PROJECTS_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

/**
 * The List Projects Module is responsible for providing the functionality of showing the projects within the root.
 */
class ListProjectsModule : Module {

    /**
     * This is the method in charge of adding the task that shows the projects within the repository.
     */
    override fun configure(project: Project) {
        project.tasks.register(LIST_PROJECTS_TASK) {
            group = MELI_GROUP
            description = LIST_PROJECTS_DESCRIPTION
            doLast {
                println(BEGINNING_TOKEN)
                printProjects(project)
            }
        }
    }

    /**
     * This is the method responsible for generating the report of all projects.
     */
    fun printProjects(project: Project) {
        for (subProject in project.subprojects) {
            println(subProject.name)
        }
    }
}
