package com.mercadolibre.android.gradle.lint

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
     * The project.
     */
    def project;

    /**
     * Array with instances of the lints to run
     */
    Lint[] linters;

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

        // The task to which the lint will be hooked
        def TASK_TO_HOOK = "assemble"

        /**
         * Creation of the lint task
        */
        def task = project.tasks.create TASK_NAME
        task.setDescription("Lints the project dependencies to check they are in the allowed whitelist")
        task.doLast {
            def buildErrored = false
            linters.each {
                println ":${project.name}:${it.name()}"
                def lintErrored = it.lint(project)
                if (lintErrored) {
                    buildErrored = true
                }
            }

            if (buildErrored) {
                throw new GradleException("Errors found while running lints, please check the console output for more information")
            }
        }

        /**
         * Hooking of the task
        */
        project.tasks.whenTaskAdded { addedTask ->
            if (addedTask.name.contains(TASK_TO_HOOK)) {
                addedTask.dependsOn TASK_NAME
            }
        }
    }

}
