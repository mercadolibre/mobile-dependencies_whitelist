package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Created by saguilera on 7/22/17.
 */
class JavaJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        project.afterEvaluate {
            project.tasks.findByName("jacocoFullReport").dependsOn project.tasks.findByName("jacocoTestReport")
        }
    }

}
