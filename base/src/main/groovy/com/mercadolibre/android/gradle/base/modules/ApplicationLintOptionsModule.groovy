package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Module that configure lint options for android application modules
 *
 * Created by lcaramelo on 05/08/20.
 */
class ApplicationLintOptionsModule implements Module {

    @Override
    void configure(Project project) {
        project.with {
            android {
                lintOptions {
                    checkDependencies true
                    //TODO: Remove this when FloathMath AGP's lint error in SCA config files has been fixed
                    disable 'LintError'
                }
            }            
        }
    }

}