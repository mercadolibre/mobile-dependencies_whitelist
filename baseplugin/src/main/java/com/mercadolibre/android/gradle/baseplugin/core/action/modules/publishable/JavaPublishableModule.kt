package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable.PublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishJarExperimentalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishJarLocalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishJarPrivateReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes.PublishJarPublicReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_JAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCE_SETS_DEFAULT
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCE_SETS_TEST
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_LOCAL
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_PRIVATE_RELEASE
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_PUBLIC_RELEASE
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_TYPE_RELEASE
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * JavaPublishableModule is in charge of generating all the tasks of a project to publish the modules.
 */
class JavaPublishableModule : PublishableModule() {

    override fun configure(project: Project) {
        super.configure(project)
        findExtension<SourceSetContainer>(project)?.apply {
            all {
                addTask(project, this)
            }
        }
    }

    private fun createTask(task: PublishJarTask, libraryVariant: SourceSet, theTaskName: String, project: Project) {
        task.register(project, libraryVariant, theTaskName)
    }

    fun addTask(project: Project, variant: SourceSet) {
        val variantName = variant.name
        if (variantName != SOURCE_SETS_TEST) {
            val taskTypes = mutableMapOf(
                PublishJarPrivateReleaseTask() to TASK_TYPE_RELEASE,
                PublishJarExperimentalTask() to TASK_TYPE_EXPERIMENTAL,
                PublishJarLocalTask() to TASK_TYPE_LOCAL,
                PublishJarPrivateReleaseTask() to TASK_TYPE_PRIVATE_RELEASE,
                PublishJarPublicReleaseTask() to TASK_TYPE_PUBLIC_RELEASE

            )

            for (task in taskTypes) {
                createTask(task.key, variant, getTaskName(task.value, PACKAGING_JAR_CONSTANT, variantName), project)
            }
        }

        if (variantName == SOURCE_SETS_DEFAULT) {

            val taskTypes = mutableMapOf(
                PublishJarPrivateReleaseTask() to TASK_TYPE_RELEASE,
                PublishJarExperimentalTask() to TASK_TYPE_EXPERIMENTAL,
                PublishJarLocalTask() to TASK_TYPE_LOCAL,
                PublishJarPrivateReleaseTask() to TASK_TYPE_PRIVATE_RELEASE,
                PublishJarPublicReleaseTask() to TASK_TYPE_PUBLIC_RELEASE,
            )

            for (task in taskTypes) {
                createTask(task.key, variant, getTaskName(task.value, PACKAGING_JAR_CONSTANT), project)
            }

            val taskTypesMirror = mutableMapOf(
                PublishJarPrivateReleaseTask() to TASK_TYPE_RELEASE,
                PublishJarExperimentalTask() to TASK_TYPE_EXPERIMENTAL,
                PublishJarLocalTask() to TASK_TYPE_LOCAL,
                PublishJarPrivateReleaseTask() to TASK_TYPE_PRIVATE_RELEASE,
                PublishJarPublicReleaseTask() to TASK_TYPE_PUBLIC_RELEASE,
            )

            for (task in taskTypesMirror) {
                createTask(task.key, variant, getTaskName(task.value), project)
            }
        }
    }
}
