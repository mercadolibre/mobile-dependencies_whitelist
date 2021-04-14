package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Module that configure lint options for disabling LintError
 * TODO: Remove this when FloathMath AGP's lint error in SCA config files has been fixed
 */
class LintErrorDisableModule implements Module {

    @Override
    void configure(Project project) {
        project.with {
            android {
                lintOptions {
                    disable 'LintError'
                }
            }            
        }
    }
}