package com.mercadolibre.android.gradle.library.core.action.modules.publishable

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable.PublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishAarExperimentalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishAarLocalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishAarPrivateReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishAarPublicReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_AAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.RELEASE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_LOCAL
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_PRIVATE_RELEASE
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_PUBLIC_RELEASE
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_RELEASE
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * LibraryPublishableModule is in charge of generating all the tasks of a project to publish the modules.
 */
class LibraryPublishableModule : PublishableModule() {

    /**
     * This method is in charge of requesting that all publication tasks be added.
     */
    override fun configure(project: Project) {
        super.configure(project)
        findExtension<LibraryExtension>(project)?.apply {
            libraryVariants.all {
                createTasksFor(this, project)
            }
        }
    }

    /**
     * This method is in charge of generating all the publishing tasks for the different types of Build Variants.
     */
    fun createTasksFor(libraryVariant: LibraryVariant, project: Project) {
        val variantName = libraryVariant.name

        val experimentalTask = createTask(
            PublishAarExperimentalTask(), libraryVariant,
            getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING_AAR_CONSTANT, variantName), project
        )
        // "Release" Task name maintained for retrocompatibility
        val releaseTask = createTask(
            PublishAarPrivateReleaseTask(), libraryVariant,
            getTaskName(TASK_TYPE_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
        )
        val privateReleaseTask = createTask(
            PublishAarPrivateReleaseTask(), libraryVariant,
            getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
        )
        val localTask = createTask(
            PublishAarLocalTask(), libraryVariant,
            getTaskName(TASK_TYPE_LOCAL, PACKAGING_AAR_CONSTANT, variantName), project
        )
        val publicReleaseTask = createTask(
            PublishAarPublicReleaseTask(), libraryVariant,
            getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
        )

        if (libraryVariant.name.toLowerCase().contains(RELEASE_CONSTANT)) {
            // Create tasks without the variant suffix that default to the main sourcesets
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING_AAR_CONSTANT), experimentalTask, project)
            // "Release" Task name maintained for retrocompatibility
            createStubTask(getTaskName(TASK_TYPE_RELEASE, PACKAGING_AAR_CONSTANT), releaseTask, project)
            createStubTask(getTaskName(TASK_TYPE_LOCAL, PACKAGING_AAR_CONSTANT), localTask, project)
            createStubTask(getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING_AAR_CONSTANT), privateReleaseTask, project)
            createStubTask(getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING_AAR_CONSTANT), publicReleaseTask, project)

            // Create tasks without the variant and package type suffix, defaulting to release
            createStubTask(getTaskName(TASK_TYPE_EXPERIMENTAL), experimentalTask, project)
            // "Release" Task name maintained for retrocompatibility
            createStubTask(getTaskName(TASK_TYPE_RELEASE), releaseTask, project)
            createStubTask(getTaskName(TASK_TYPE_LOCAL), localTask, project)
            createStubTask(getTaskName(TASK_TYPE_PRIVATE_RELEASE), privateReleaseTask, project)
            createStubTask(getTaskName(TASK_TYPE_PUBLIC_RELEASE), publicReleaseTask, project)
        }
    }

    private fun createTask(task: PublishAarTask, libraryVariant: BaseVariant, theTaskName: String, project: Project): TaskProvider<Task> {
        return task.register(project, libraryVariant, theTaskName)
    }

    private fun createStubTask(name: String, realTask: TaskProvider<Task>?, project: Project) {
        if (project.tasks.names.contains(name) && realTask != null) {
            project.tasks.named(name).configure {
                dependsOn(realTask)
            }
        } else {
            project.tasks.register(name) {
                group = PUBLISHING_GROUP
                dependsOn(realTask)
            }
        }
    }
}
