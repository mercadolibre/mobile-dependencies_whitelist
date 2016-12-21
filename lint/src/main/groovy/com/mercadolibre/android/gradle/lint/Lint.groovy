package com.mercadolibre.android.gradle.lint

/**
 * Contract that custom gradle lints should implement
 *
 * Created by saguilera on 12/21/16.
 */
interface Lint {

    /**
     * Return a string with the name of the lint task
     */
    def name()

    /**
     * Lint the project
     * Returns boolean notifying if the lint contained errors or not
     */
    def lint(def project)

}