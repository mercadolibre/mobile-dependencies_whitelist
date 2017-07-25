package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.*
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by saguilera on 7/21/17.
 */
class AndroidLibraryPublishableModule extends PublishableModule {

    private static final String PACKAGING = 'Aar'

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
        String variantName = libraryVariant.name

        def alphaTask = createTask(new PublishAarAlphaTask(), libraryVariant,
                getTaskName(TASK_TYPE_ALPHA, PACKAGING, variantName))
        def experimentalTask = createTask(new PublishAarExperimentalTask(), libraryVariant,
                getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING, variantName))
        def releaseTask = createTask(new PublishAarReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_RELEASE, PACKAGING, variantName))
        def localTask = createTask(new PublishAarLocalTask(), libraryVariant,
                getTaskName(TASK_TYPE_LOCAL, PACKAGING, variantName))

        if (libraryVariant.name.toLowerCase().contains(TASK_TYPE_RELEASE.toLowerCase())) {
            // Create tasks without the variant suffix that default to the main sourcesets
            createStubTask(getTaskName(TASK_TYPE_ALPHA, PACKAGING), alphaTask)
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING), experimentalTask)
            createStubTask(getTaskName(TASK_TYPE_RELEASE, PACKAGING), releaseTask)
            createStubTask(getTaskName(TASK_TYPE_LOCAL, PACKAGING), localTask)

            // Create tasks without the variant and package type suffix, defaulting to release
            createStubTask(getTaskName(TASK_TYPE_ALPHA), alphaTask)
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL), experimentalTask)
            createStubTask(getTaskName(TASK_TYPE_RELEASE), releaseTask)
            createStubTask(getTaskName(TASK_TYPE_LOCAL), localTask)
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
                it.group = PublishTask.TASK_GROUP
                it.dependsOn realTask
            }
        }
        return project.tasks."$name"
    }

}
