package com.mercadolibre.android.gradle.base.modules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

class AndroidLibraryTestableModule implements Module {
    private static final UNIT_TEST_FLAVOR_TEST_TASK_NAME = 'test${variant}UnitTest'
    private static final UNIT_TEST_TASK_GROUP = 'verification'
    private static final UNIT_TEST_TASK_DESCRIPTION = 'Run unit tests for the ${build} build.'

    private static final JACOCO_REPORT_FLAVOR_TEST_TASK_NAME = 'jacocoTest${variant}UnitTestReport'
    private static final JACOCO_REPORT_TASK_GROUP = 'reporting'
    private static final JACOCO_REPORT_TASK_DESCRIPTION = 'Generates Jacoco coverage reports for the ${build} variant.'

    private static final TASK_REGEX = '(?:${flavor})([A-Z0-9][a-z0-9_-]+)'
    private final TASKS = [new FlavorTesteableTask(UNIT_TEST_FLAVOR_TEST_TASK_NAME, UNIT_TEST_TASK_GROUP, UNIT_TEST_TASK_DESCRIPTION),
                           new FlavorTesteableTask(JACOCO_REPORT_FLAVOR_TEST_TASK_NAME, JACOCO_REPORT_TASK_GROUP, JACOCO_REPORT_TASK_DESCRIPTION)]

    private static final ENGINE = new SimpleTemplateEngine()

    @Override
    void configure(Project project) {
        project.android.productFlavors.all { flavor ->
            project.android.libraryVariants.all { variant ->
                String variantName = variant.name.capitalize()
                String flavorName = flavor.name.capitalize()
                if (!variantName.startsWith(flavorName)) {
                    return
                }

                TASKS.forEach({ task ->
                    String regex = ENGINE.createTemplate(TASK_REGEX).make(["flavor": flavorName]).toString()
                    String build = (variantName =~ regex)[0][1]
                    String flavorTaskName = ENGINE.createTemplate(task.taskName).make(["variant": variantName]).toString()
                    String genericTaskName = ENGINE.createTemplate(task.taskName).make(["variant": build]).toString()

                    TaskProvider<Task> flavorTask = project.tasks.named(flavorTaskName)

                    if (project.tasks.names.contains(genericTaskName)) {
                        project.tasks.named(genericTaskName).configure {
                            dependsOn flavorTask
                        }
                    } else {
                        project.tasks.register(genericTaskName).configure { Task it ->
                            it.group = task.group
                            it.description = ENGINE.createTemplate(task.description).make(["build": build]).toString()
                            it.dependsOn flavorTask
                        }
                    }
                })
            }
        }
    }

    private class FlavorTesteableTask {
        private String taskName
        private String group
        private String description

        private FlavorTesteableTask(String taskName, String group, String description) {
            this.taskName = taskName
            this.group = group
            this.description = description
        }
    }
}
