package com.mercadolibre.android.gradle.lint

import org.gradle.api.tasks.StopActionException
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import com.mercadolibre.android.gradle.lint.dependencies.DependenciesLint

/**
 * Gradle plugin for Android Lints. It provides some important tasks:
 *
 * <ol>
 *     <li>Create custom lint gradle rules.</li>
 *     <li>Create lint reports of the dependencies allowed.</li>
 * </ol>
 *
 * @author Santiago Aguilera
 */
class LintPlugin implements Plugin<Project> {

    /**
     * Gradle lint task name
     */
    private static final String TASK_NAME = "lintGradle"
    
    /**
     * Error message when failing
     */
    private static final String TASK_FAIL_MESSAGE = "Errors found while running lints, please check the console output for more information"

    /**
     * The project.
     */
    def project

    /**
     * Array with instances of the lints to run
     */
    Lint[] linters

    /**
     * Array with available variants
     *
     * We have to save them before, else gradle
     * throws us a tantrum if accessing them after
     * tasks are created
     */
    List<Object> variants

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {
        this.project = project
        setUpLint()
    }

    /**
     * Set up gradle lint to run in every assemble task.
     */
    def setUpLint() {
        /**
         * Array with the lints to run
         */
        linters = [
            new DependenciesLint()
        ]

        variants = new ArrayList<>()
        project.android {
            defaultConfig {
                libraryVariants.all { variant -> variants.add(variant) }
            }
        }

        // The tasks to which the lint will be hooked
        def TASK_ASSEMBLE = "assemble" // This is when the library is assembled.
        def TASK_BUNDLE = "bundle" // This is if an application that depends on is assembled.

        /**
         * Creation of the lint task
         */
        def task = project.tasks.create TASK_NAME
        task.setDescription("Lints the project dependencies to check they are in the allowed whitelist")
        task.doLast {
            def buildErrored = false
            linters.each {
                println ":${project.name}:${it.name()}"
                def lintErrored = it.lint(project, variants)
                if (lintErrored) {
                    buildErrored = true
                }
            }

            if (buildErrored) {
                if (project.gradle.getStartParameter().getTaskRequests().toString().contains(TASK_NAME)) {
                    // If they are specifically running lint, break the build and error it
                    throw new GradleException(TASK_FAIL_MESSAGE)
                } else {
                    // If they are assembling, let them know about the errors but dont fail the build
                    throw new StopActionException(TASK_FAIL_MESSAGE)
                }
            }
        }

        /**
         * Hooking of the task
         */
        project.tasks.whenTaskAdded { addedTask ->
            if (addedTask.name.contains(TASK_ASSEMBLE) || addedTask.name.contains(TASK_BUNDLE)) {
                addedTask.dependsOn TASK_NAME
            }
        }
    }

}
