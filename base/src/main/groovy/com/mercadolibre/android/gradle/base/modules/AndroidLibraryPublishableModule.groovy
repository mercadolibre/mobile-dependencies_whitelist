package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.*
import org.gradle.api.Project

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
        createTasks()
    }

    private void applyPlugins() {
        project.apply plugin: 'com.github.dcendents.android-maven'
    }

    protected void createTasksFor(def libraryVariant) {
        createTask(new PublishAarAlphaTask(), libraryVariant, "publishAarAlpha${libraryVariant.name.capitalize()}")
        createTask(new PublishAarExperimentalTask(), libraryVariant, "publishAarExperimental${libraryVariant.name.capitalize()}")
        createTask(new PublishAarReleaseTask(), libraryVariant, "publishAarRelease${libraryVariant.name.capitalize()}")
        createTask(new PublishAarLocalTask(), libraryVariant, "publishAarLocal${libraryVariant.name.capitalize()}")

        if (libraryVariant.name == 'release') {
            // Create tasks without the variant suffix that default to the main sourcesets
            createTask(new PublishAarAlphaTask(), libraryVariant, "publishAarAlpha")
            createTask(new PublishAarExperimentalTask(), libraryVariant, "publishAarExperimental")
            createTask(new PublishAarReleaseTask(), libraryVariant, "publishAarRelease")
            createTask(new PublishAarLocalTask(), libraryVariant, "publishAarLocal")

            // Create tasks without the variant and package type suffix, defaulting to release
            createTask(new PublishAarAlphaTask(), libraryVariant, "publishAlpha")
            createTask(new PublishAarExperimentalTask(), libraryVariant, "publishExperimental")
            createTask(new PublishAarReleaseTask(), libraryVariant, "publishRelease")
            createTask(new PublishAarLocalTask(), libraryVariant, "publishLocal")
        }
    }

    protected void createTask(PublishAarTask task, def libraryVariant, String theTaskName) {
        task.create(new PublishTask.Builder().with {
            project = this.project
            variant = libraryVariant
            taskName = theTaskName
            return it
        })
    }

    private void createTasks() {
        project.android.libraryVariants.all { def libraryVariant ->
            createTasksFor(libraryVariant)
        }
    }

}
