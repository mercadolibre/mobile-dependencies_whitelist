package com.mercadolibre.android.gradle.base.lint

import org.gradle.api.Project

/**
 * Contract that custom gradle lints should implement
 */
interface Lint {

    static final String LINT_FILENAME = "lint.ld"
    static final String LINT_WARNING_FILENAME = "lintWarning.ld"

    /**
     * Return a string with the name of the lint task
     */
    String name()

    /**
     * Lint the project
     * Returns boolean notifying if the lint contained errors or not
     */
    boolean lint(Project project, def variants)

}