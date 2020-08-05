package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

class ApplicationLintOptionsModule implements Module {

    @Override
    void configure(Project project) {
        project.with {
            android {
                lintOptions {
                    checkDependencies true
                }
            }            
        }
    }

}