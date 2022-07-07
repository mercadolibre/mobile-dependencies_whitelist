package com.mercadolibre.android.gradle.baseplugin.core.action.modules.project_version

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
    override fun configure(project: Project) {
        val task = project.tasks.register(TASK_GET_PROJECT_TASK)
        task.configure {
            description = TASK_GET_PROJECT_DESCRIPTION

            doLast {
                printProjectVersion(project)
            }
        }
    }

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
