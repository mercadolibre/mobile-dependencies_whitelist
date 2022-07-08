package com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectVersion

import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.FILE_NAME_PROJECT_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import java.io.File

/**
 * ProjectVersion Module is in charge of storing the version of the project so that it can be collected.
 */
class ProjectVersionModule : Module {

    /**
     * This method is in charge of generating the task that will show the version report.
     */
    override fun configure(project: Project) {
        val task = project.tasks.register(TASK_GET_PROJECT_TASK)
        task.configure {
            description = TASK_GET_PROJECT_DESCRIPTION

            doLast {
                printProjectVersion(project)
            }
        }
    }

    /**
     * This method is in charge of creating the file and generating the version report.
     */
    fun printProjectVersion(project: Project) {
        val folder = File(BUILD_CONSTANT)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val inputFile = File("$folder/$FILE_NAME_PROJECT_VERSION")
        inputFile.writeText("version: ${project.version}")
        println("See $folder/$FILE_NAME_PROJECT_VERSION file")
    }
}
