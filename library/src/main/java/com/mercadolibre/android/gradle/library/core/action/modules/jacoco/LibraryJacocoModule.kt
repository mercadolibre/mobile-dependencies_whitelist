package com.mercadolibre.android.gradle.library.core.action.modules.jacoco

import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.AndroidJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

/**
 * This module is in charge of configuring the Jacoco tasks for their correct operation.
 */
class LibraryJacocoModule : AndroidJacocoModule() {
    override fun configure(project: Project) {
        super.configure(project)

        findExtension<JacocoTaskExtension>(project)?.apply {
            isIncludeNoLocationClasses = true
        }

        val jacocoTestReportTask = findOrCreateJacocoTestReportTask(project)

        project.afterEvaluate {
            findExtension<LibraryExtension>(project)?.apply {
                for (variant in libraryVariants) {
                    val reportTask = createReportTask(variant, project)
                    jacocoTestReportTask.configure {
                        dependsOn(reportTask)
                    }
                }
            }
        }

        project.tasks.named(JACOCO_FULL_REPORT_TASK).configure {
            dependsOn(jacocoTestReportTask)
        }
    }
}
