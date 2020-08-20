package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Module in charge of configuring a task that list subprojects of this project. It can filter Android's applications from libraries.
 * For doing so, a parameter must be added to the execution as a project parameter like -Ptype=[DESIRED_TYPE]
 */
class ListProjectsModule implements Module {
    public static final String ANDROID_LIBRARY_PLUGIN = 'com.android.library'
    public static final String ANDROID_APPLICATION_PLUGIN = 'com.android.application'

    private static final String LIST_PROJECTS_TASK_NAME = "listProjects"
    private static final String LIST_PROJECTS_TASK_DESCRIPTION = "List all subprojects in this project'"
    private static final String TYPE_PROPERTY_NAME = "type"

    private static final String BEGINNING_TOKEN = "=== BEGINNING OF PROJECTS LIST ==="
    private static final String TYPE_NOT_RECOGNISED_MESSAGE = "Specified project type not recognised. Project types available are " + AndroidProjectTypes.values()

    enum AndroidProjectTypes {
        APPLICATION,
        LIBRARY
    }

    @Override
    void configure(final Project project) {
        project.tasks.register(LIST_PROJECTS_TASK_NAME) { Task it ->
            setDescription(LIST_PROJECTS_TASK_DESCRIPTION)
            doLast {
                println(BEGINNING_TOKEN)
                try {
                    switch (AndroidProjectTypes.valueOf((project.property(TYPE_PROPERTY_NAME) as String).toUpperCase())) {
                        case AndroidProjectTypes.APPLICATION:
                            printSubprojectsNameByType(project, ANDROID_APPLICATION_PLUGIN)
                            break
                        case AndroidProjectTypes.LIBRARY:
                            printSubprojectsNameByType(project, ANDROID_LIBRARY_PLUGIN)
                            break
                    }
                } catch (final MissingPropertyException ignored) {
                    project.subprojects.forEach { println(it.name) }
                } catch (final IllegalArgumentException ignored) {
                    throw new GradleException(TYPE_NOT_RECOGNISED_MESSAGE)
                }
            }
        }
    }

    private void printSubprojectsNameByType(Project project, String plugin) {
        project.subprojects.forEach {
            if (it.plugins.hasPlugin(plugin)) {
                println(it.name)
            }
        }
    }
}
