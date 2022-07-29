package com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectVersion

import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.FILE_NAME_PROJECT_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

/**
 * ProjectVersion Module is in charge of storing the version of the project so that it can be collected.
 */
class ProjectVersionModule : Module {

    /**
     * This method is in charge of generating the task that will show the version report.
     */
    override fun configure(project: Project) {
        configureTask(project.tasks.register(TASK_GET_PROJECT_TASK).get(), project)
    }

    private fun configureTask(task: Task, project: Project) {
        with(task) {
            group = MELI_GROUP
            description = TASK_GET_PROJECT_DESCRIPTION
            doLast {
                printProjectVersion(File(BUILD_CONSTANT), File("$BUILD_CONSTANT/$FILE_NAME_PROJECT_VERSION"), project)
            }
        }
    }

    /**
     * This method is in charge of creating the file and generating the version report.
     */
    fun printProjectVersion(buildFile: File, inputFile: File, project: Project) {
        if (!buildFile.exists()) {
            buildFile.mkdirs()
        }

        inputFile.writeText("version: ${project.version}")
        println("See $buildFile/$FILE_NAME_PROJECT_VERSION file")
    }
}
