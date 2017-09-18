package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.BasePlugin
import com.mercadolibre.android.gradle.base.lint.Lint
import com.mercadolibre.android.gradle.base.lint.LintGradleExtension
import com.mercadolibre.android.gradle.base.lint.dependencies.DependenciesLint
import com.mercadolibre.android.gradle.base.lint.dependencies.ReleaseDependenciesLint
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
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
    protected Project project

    /**
     * Array with instances of the lints to run
     */
    protected final Lint[] linters = [
            new DependenciesLint(),
            new ReleaseDependenciesLint()
    ]

    /**
     * Array of tasks that this lint depends of
     */
    protected final String[] dependencies = [
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
    protected final List variants = new ArrayList()

    @Override
    void configure(Project project) {
        this.project = project

        project.afterEvaluate {
            setUpLint()
        }

        project.tasks.whenTaskAdded { addedTask ->
            if (project.tasks.findByName(TASK_NAME) && dependencies.contains(addedTask.name)) {
                addedTask.dependsOn TASK_NAME
            }
        }
    }

    static void createExtension(Project project) {
        String extensionName = LintGradleExtension.simpleName.replaceAll("Extension", '')
        extensionName = (Character.toLowerCase(extensionName.charAt(0)) as String) + extensionName.substring(1)

        project.extensions.create(extensionName, LintGradleExtension)
    }

    /**
     * Set up gradle lint to run in every assemble task.
     */
    protected void setUpLint() {
        /**
         * Inflate variants according to the type of project
         */
        project.plugins.withId(BasePlugin.ANDROID_LIBRARY_PLUGIN) {
            project.android.libraryVariants.all { variants.add(it) }
        }
        project.plugins.withId(BasePlugin.ANDROID_APPLICATION_PLUGIN) {
            project.android.applicationVariants.all { variants.add(it) }
        }
        project.plugins.withType(JavaPlugin) {
            project.sourceSets.each { variants.add(it) }
        }

        /**
         * Creation of the lint task
         */
        project.task (TASK_NAME) { Task task ->
            task.description "Lints the project dependencies to check they are in the allowed whitelist"
            task.doLast {
                def buildErrored = false

                if (project.rootProject.lintGradle.enabled) {
                    linters.each {
                        println ":${project.name}:${it.name()}"
                        def lintErrored = it.lint(project, variants)
                        if (lintErrored) {
                            buildErrored = true
                        }
                    }
                }

                if (buildErrored) {
                    if (project.gradle.startParameter.taskNames.toListString().contains(task.name)) {
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
