package com.mercadolibre.android.gradle.library.core.action.modules.jacoco

import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.AndroidJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * This module is in charge of configuring the Jacoco tasks for their correct operation.
 */
class LibraryJacocoModule : AndroidJacocoModule() {

    /**
     * This method configures the execution time of the module.
     */
    override fun executeInAfterEvaluate(): Boolean = false

    /**
     * This method configures the Jacoco Task extension and configures all the tasks that jacoco depends on to check coverage.
     */
    override fun configure(project: Project) {
        super.configure(project)

        val jacocoTestReportTask = findOrCreateJacocoTestReportTask(project)

        project.afterEvaluate {
            configVariantsTasks(project, jacocoTestReportTask)
        }

        project.tasks.named(JACOCO_FULL_REPORT_TASK).configure {
            dependsOn(jacocoTestReportTask)
        }
    }

    /**
     * This method configures all the tasks that jacoco depends on to check coverage.
     */
    fun configVariantsTasks(project: Project, jacocoTestReportTask: TaskProvider<Task>) {
        findExtension<LibraryExtension>(project)?.apply {
            for (variant in libraryVariants) {
                jacocoTestReportTask.get().dependsOn(createReportTask(variant, project))
            }
        }
    }
}
