package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPlugin

/**
 * Created by saguilera on 7/22/17.
 */
abstract class BaseJacocoModule extends Module {

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

            task("jacocoFullReport") {
                it.group 'reporting'
            }
        }
    }

}
