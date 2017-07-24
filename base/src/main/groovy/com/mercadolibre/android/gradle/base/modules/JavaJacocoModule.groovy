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
            if (project.tasks.findByName('jacocoFullReport')) {
                project.tasks.jacocoFullReport.dependsOn jacocoTestReport
            } else {
                project.tasks.whenTaskAdded {
                    if (it.name.contentEquals('jacocoFullReport')) {
                        it.dependsOn jacocoTestReport
                    }
                }
            }
        }
    }

}
