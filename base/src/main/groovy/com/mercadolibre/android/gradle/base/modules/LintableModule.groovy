package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.BasePlugin
import com.mercadolibre.android.gradle.base.lint.Lint
import com.mercadolibre.android.gradle.base.lint.LintGradleExtension
import com.mercadolibre.android.gradle.base.lint.dependencies.LibraryWhitelistedDependenciesLint
import com.mercadolibre.android.gradle.base.lint.dependencies.ReleaseDependenciesLint
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

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
            new LibraryWhitelistedDependenciesLint(),
            new ReleaseDependenciesLint()
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
        project.tasks.create(TASK_NAME, {
            description "Lints the project dependencies to check they are in the allowed whitelist"
            doLast {
                if (project.lintGradle.enabled) {
                    def buildErrored = false
                    linters.each {
                        println ":${project.name}:${it.name()}"
                        def lintErrored = it.lint(project, variants)
                        if (lintErrored) {
                            buildErrored = true
                        }
                    }

                    if (buildErrored) {
                        throw new GradleException(TASK_FAIL_MESSAGE)
                    }
                }
            }
        })

        if (project.tasks.findByName('check')) {
            project.tasks.check.dependsOn TASK_NAME
        } else {
            project.tasks.whenTaskAdded {
                if (it.name == 'check') {
                    it.dependsOn TASK_NAME
                }
            }
        }

    }

}
