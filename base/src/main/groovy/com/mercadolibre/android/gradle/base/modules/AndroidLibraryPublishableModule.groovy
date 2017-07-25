package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.*
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by saguilera on 7/21/17.
 */
class AndroidLibraryPublishableModule extends PublishableModule {

    private Project project

    @Override
    void configure(Project project) {
        super.configure(project)

        this.project = project

        applyPlugins()

        project.android.libraryVariants.all { def libraryVariant ->
            createTasksFor(libraryVariant)
        }
    }

    private void applyPlugins() {
        project.apply plugin: 'com.github.dcendents.android-maven'
    }

    protected void createTasksFor(def libraryVariant) {
        def alphaTask = createTask(new PublishAarAlphaTask(), libraryVariant, "publishAarAlpha${libraryVariant.name.capitalize()}")
        def experimentalTask = createTask(new PublishAarExperimentalTask(), libraryVariant, "publishAarExperimental${libraryVariant.name.capitalize()}")
        def releaseTask = createTask(new PublishAarReleaseTask(), libraryVariant, "publishAarRelease${libraryVariant.name.capitalize()}")
        def localTask = createTask(new PublishAarLocalTask(), libraryVariant, "publishAarLocal${libraryVariant.name.capitalize()}")

        if (libraryVariant.name.toLowerCase().contains('release')) {
            // Create tasks without the variant suffix that default to the main sourcesets
            createStubTask("publishAarAlpha", alphaTask)
            createStubTask("publishAarExperimental", experimentalTask)
            createStubTask("publishAarRelease", releaseTask)
            createStubTask("publishAarLocal", localTask)

            // Create tasks without the variant and package type suffix, defaulting to release
            createStubTask("publishAlpha", alphaTask)
            createStubTask("publishExperimental", experimentalTask)
            createStubTask("publishRelease", releaseTask)
            createStubTask("publishLocal", localTask)
        }
    }

    protected Task createTask(PublishAarTask task, def libraryVariant, String theTaskName) {
        return task.create(new PublishTask.Builder().with {
            project = this.project
            variant = libraryVariant
            taskName = theTaskName
            return it
        })
    }

    protected Task createStubTask(String name, Task realTask) {
        if (project.tasks.findByName(name)) {
            project.tasks."$name".dependsOn realTask
        } else {
            project.task(name) { Task it ->
                it.group = 'publishing'
                it.dependsOn realTask
            }
        }
        return project.tasks."$name"
    }

}
