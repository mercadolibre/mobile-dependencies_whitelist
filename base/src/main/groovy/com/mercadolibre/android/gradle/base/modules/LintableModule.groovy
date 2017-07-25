package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.lint.Lint
import com.mercadolibre.android.gradle.base.lint.LintConfigurationExtension
import com.mercadolibre.android.gradle.base.lint.dependencies.DependenciesLint
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.StopActionException

/**
 * Module that applies lints to android libraries
 *
 * Created by saguilera on 7/22/17.
 */
class LintableModule implements Module {

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
    Lint[] linters = [
            new DependenciesLint()
    ]

    /**
     * Array of tasks that this lint depends of
     */
    String[] dependencies = [
            'assemble',
            'bundle'
    ]

    /**
     * Array with available variants
     *
     * We have to save them before, else gradle
     * throws us a tantrum if accessing them after
     * tasks are created
     */
    List<Object> variants

    @Override
    void configure(Project project) {
        this.project = project

        project.extensions.create(LintConfigurationExtension.name.replaceAll("Extension", '').uncapitalize(),
                LintConfigurationExtension)

        project.afterEvaluate {
            setUpLint()
        }

        project.tasks.whenTaskAdded { addedTask ->
            if (project.tasks.findByName(TASK_NAME) && dependencies.contains(addedTask.name)) {
                addedTask.dependsOn TASK_NAME
            }
        }
    }

    /**
     * Set up gradle lint to run in every assemble task.
     */
    protected void setUpLint() {
        variants = new ArrayList<>()
        project.android.libraryVariants.all { variant -> variants.add(variant) }

        /**
         * Creation of the lint task
         */
        project.task (TASK_NAME) {
            description "Lints the project dependencies to check they are in the allowed whitelist"
            doLast {
                def buildErrored = false

                if (project.lintConfiguration.enabled) {
                    linters.each {
                        println ":${project.name}:${it.name()}"
                        def lintErrored = it.lint(project, variants)
                        if (lintErrored) {
                            buildErrored = true
                        }
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
        }
    }

}
