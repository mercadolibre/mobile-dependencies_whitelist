package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Java module for customizing jacoco reports, so they can be accesed by whichever coverage application we use
 *
 * Created by saguilera on 7/22/17.
 */
class JavaJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        project.afterEvaluate {
            TaskProvider<Task> jacocoTestReport = project.tasks.named("jacocoTestReport")

            jacocoTestReport.configure {
                dependsOn project.tasks.test // For some reason its not depending on it. It should.
            }

            if (project.tasks.names.contains(JACOCO_FULL_REPORT_TASK)) {
                project.tasks.named(JACOCO_FULL_REPORT_TASK).configure {
                    dependsOn jacocoTestReport
                }
            } else {
                project.tasks.configureEach {
                    if (it.name.contentEquals(JACOCO_FULL_REPORT_TASK)) {
                        it.dependsOn jacocoTestReport
                    }
                }
            }
        }
    }
}
