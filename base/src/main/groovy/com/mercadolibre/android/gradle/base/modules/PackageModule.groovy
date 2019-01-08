package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Module in charge of creating entry values for the application ID. Android already provides by default the java field (BuildConfig.APPLICATION_ID)
 *
 * Created by saguilera on 7/22/17.
 */
class PackageModule implements Module {

    @Override
    void configure(Project project) {
        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                variant.resValue "string", "application_id_dynamic", "${variant.applicationId}"   
            }
        }
    }
}
