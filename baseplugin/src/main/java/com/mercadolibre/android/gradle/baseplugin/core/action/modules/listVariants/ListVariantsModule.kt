package com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
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
        OutputUtils.logMessage("Root Project: ${project.name}".ansi(ANSI_GREEN))
        for (subProject in project.subprojects) {
            val variants = arrayListOf<String>()

            OutputUtils.logMessage(SEPARATOR)

            var title = "${subProject.name} - "

            findExtension<LibraryExtension>(subProject)?.apply {
                title += "ModuleType: Library\n"
                variants.addAll(buildTypes.names)
            }

            findExtension<AppExtension>(subProject)?.apply {
                title += "ModuleType: App\n"
                variants.addAll(buildTypes.names)
            }

            OutputUtils.logMessage(title)

            for (variant in variants) {
                OutputUtils.logMessage(variant.ansi(ANSI_YELLOW))
            }
        }
    }
}
