package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Module in charge of configuring jacoco reports for android projects so that any coverage application can obtain them
 *
 * Created by saguilera on 7/22/17.
 */
class PackageModule implements Module {

    @Override
    void configure(Project project) {
        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                variant.resValue "string", "application_id", "\"${variant.applicationId}\""
            }
        }
    }

}
