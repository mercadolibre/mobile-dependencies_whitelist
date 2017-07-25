package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by saguilera on 7/22/17.
 */
class JavaJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        project.afterEvaluate {
            Task jacocoTestReport = project.tasks.findByName("jacocoTestReport")
            jacocoTestReport.reports {
                xml.enabled true
                xml.destination "${project.buildDir}/reports/jacoco/jacocoTestReport.xml"
                html.destination "${project.buildDir}/reports/jacoco/jacocoTestReport.html"
            }
            jacocoTestReport.dependsOn project.tasks.test // For some reason its not depending on it. It should.

            if (project.tasks.findByName(JACOCO_FULL_REPORT_TASK)) {
                project.tasks."$JACOCO_FULL_REPORT_TASK".dependsOn jacocoTestReport
            } else {
                project.tasks.whenTaskAdded {
                    if (it.name.contentEquals(JACOCO_FULL_REPORT_TASK)) {
                        it.dependsOn jacocoTestReport
                    }
                }
            }
        }
    }

}
