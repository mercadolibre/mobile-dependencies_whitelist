package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPlugin

/**
 * Base jacoco module for creating a global task that will create all the available reports for each testing task
 *
 * Created by saguilera on 7/22/17.
 */
abstract class BaseJacocoModule implements Module {

    public static final String JACOCO_FULL_REPORT_TASK = 'jacocoFullReport'

    protected Project project

    @Override
    void configure(Project project) {
        this.project = project

        project.with {
            apply plugin: JacocoPlugin

            tasks.withType(Test) {
                testLogging {
                    events "FAILED"
                    exceptionFormat "full"
                }
            }

            task(JACOCO_FULL_REPORT_TASK) {
                it.group 'reporting'
            }
        }
    }

}
