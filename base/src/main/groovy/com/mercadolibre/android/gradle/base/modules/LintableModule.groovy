package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Created by saguilera on 7/22/17.
 */
class LintableModule extends Module {

    private static final String LINT_PLUGIN_NAME = "com.mercadolibre.android.gradle.lint"

    @Override
    void configure(Project project) {
        project.apply plugin: LINT_PLUGIN_NAME
    }

}
