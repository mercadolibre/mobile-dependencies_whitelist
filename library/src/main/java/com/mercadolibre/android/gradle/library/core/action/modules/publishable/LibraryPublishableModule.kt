package com.mercadolibre.android.gradle.library.core.action.modules.publishable

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable.PublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarExperimentalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarLocalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarPrivateReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarPublicReleaseTask
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

        val tasks = mutableListOf(
            TASK_TYPE_EXPERIMENTAL to createTask(
                PublishAarExperimentalTask(), libraryVariant,
                getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING_AAR_CONSTANT, variantName), project
            ),
            TASK_TYPE_RELEASE to createTask(
                PublishAarPrivateReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
            ),
            TASK_TYPE_LOCAL to createTask(
                PublishAarLocalTask(), libraryVariant,
                getTaskName(TASK_TYPE_LOCAL, PACKAGING_AAR_CONSTANT, variantName), project
            ),
            TASK_TYPE_PRIVATE_RELEASE to createTask(
                PublishAarPrivateReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
            ),
            TASK_TYPE_PUBLIC_RELEASE to createTask(
                PublishAarPublicReleaseTask(), libraryVariant,
                getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING_AAR_CONSTANT, variantName), project
            )
        )

        if (libraryVariant.name.toLowerCase().contains(RELEASE_CONSTANT)) {
            for (task in tasks) {
                createStubTask(getTaskName(task.first, PACKAGING_AAR_CONSTANT), task.second, project)
                createStubTask(getTaskName(task.first), task.second, project)
            }
        }
    }

    private fun createTask(task: PublishAarTask, libraryVariant: BaseVariant, theTaskName: String, project: Project): TaskProvider<Task> {
        val publishTask = task.register(project, libraryVariant, theTaskName)
        publishTask.configure {
            doLast {
                TimeStampManager.deleteTimeStamp()
            }
        }
        return publishTask
    }

    /**
     * This method is in charge of generating all subtasks for the different types of Build Variants.
     */
    fun createStubTask(name: String, realTask: TaskProvider<Task>?, project: Project) {
        if (project.tasks.names.contains(name) && realTask != null) {
            project.tasks.named(name).configure {
                dependsOn(realTask)
            }
        } else {
            val task = project.tasks.register(name)
            task.get().group = PUBLISHING_GROUP
            task.get().dependsOn(realTask)
        }
    }
}
