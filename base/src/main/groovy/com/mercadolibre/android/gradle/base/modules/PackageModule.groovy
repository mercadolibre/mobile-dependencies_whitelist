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
        super.configure(project)

        project.afterEvaluate {
            final String packageName = project.android.defaultConfig.applicationId

            if (packageName == null) {
                throw new IllegalStateException("Package name not found as applicationId")
            }

            buildConfigField 'String', 'PACKAGE_NAME', "\"${packageName}\""
            resValue "string", "build_config_package_name", "\"${packageName}\""
        }
    }

}
