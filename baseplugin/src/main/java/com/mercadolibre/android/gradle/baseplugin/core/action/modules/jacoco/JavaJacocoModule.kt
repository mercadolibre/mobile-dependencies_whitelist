package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain.BaseJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.TEST_TASK
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Java Jacoco Module is in charge of providing the functionality for java modules of the jacoco reports for the coverage
 * and the files that CI needs.
 */
class JavaJacocoModule : BaseJacocoModule() {

    override fun configure(project: Project) {
        super.configure(project)

        project.afterEvaluate {
            configureProejct(project)
        }
    }

    fun configureProejct(project: Project) {
        with(project) {
            val jacocoTestReport = project.tasks.named(JACOCO_TEST_REPORT_TASK)

            jacocoTestReport.configure {
                configureTestReport(this as JacocoReport)
                dependsOn(project.tasks.named(TEST_TASK))
            }

            if (project.tasks.names.contains(JACOCO_FULL_REPORT_TASK)) {
                project.tasks.named(JACOCO_FULL_REPORT_TASK).configure {
                    dependsOn(jacocoTestReport)
                }
            } else {
                project.tasks.configureEach {
                    if (name.contentEquals(JACOCO_FULL_REPORT_TASK)) {
                        dependsOn(jacocoTestReport)
                    }
                }
            }
        }
    }

    fun configureTestReport(task: JacocoReport) {
        with(task) {

            group = JACOCO_GROUP

            reports {
                xml.required.set(true)
                csv.required.set(true)
                html.required.set(true)
            }
        }
    }
}
