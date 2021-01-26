package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Android library publishing module. It will create tasks for each available variant and flavor.
 *
 * Currently it will create publish tasks for RELEASE / EXPERIMENTAL / LOCAL
 *
 * Created by saguilera on 7/21/17.
 */
class AndroidLibraryPublishableModule extends PublishableModule {

    public static final String PACKAGING = 'Aar'
    public static final String RELEASE_VARIANT = 'release'

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

        def experimentalTask = createTask(new PublishAarExperimentalTask(), libraryVariant,
                getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING, variantName))
        def privateReleaseTask = createTask(new PublishAarPrivateReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING, variantName))
        def localTask = createTask(new PublishAarLocalTask(), libraryVariant,
                getTaskName(TASK_TYPE_LOCAL, PACKAGING, variantName))
        def publicReleaseTask = createTask(new PublishAarPublicReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING, variantName))

        if (libraryVariant.name.toLowerCase().contains(RELEASE_VARIANT)) {
            // Create tasks without the variant suffix that default to the main sourcesets
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING), experimentalTask)
            createStubTask(getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING), privateReleaseTask)
            createStubTask(getTaskName(TASK_TYPE_LOCAL, PACKAGING), localTask)
            createStubTask(getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING), publicReleaseTask)

            // Create tasks without the variant and package type suffix, defaulting to release
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL), experimentalTask)
            createStubTask(getTaskName(TASK_TYPE_PRIVATE_RELEASE), privateReleaseTask)
            createStubTask(getTaskName(TASK_TYPE_LOCAL), localTask)
            createStubTask(getTaskName(TASK_TYPE_PUBLIC_RELEASE), publicReleaseTask)
        }
    }

    protected TaskProvider<Task> createTask(PublishAarTask task, def libraryVariant, String theTaskName) {
        return task.register(new PublishTask.Builder().with {
            project = this.project
            variant = libraryVariant
            taskName = theTaskName
            return it
        })
    }

    protected void createStubTask(String name, TaskProvider<Task> realTask) {
        if (project.tasks.names.contains(name)) {
            project.tasks.named(name).configure {
                dependsOn realTask
            }
        } else {
            project.tasks.register(name) { Task it ->
                it.group = PublishTask.TASK_GROUP
                it.dependsOn realTask
            }
        }
    }

}
