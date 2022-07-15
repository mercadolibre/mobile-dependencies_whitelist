package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
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
        task.register(project, libraryVariant, theTaskName).configure {
            doLast {
                TimeStampManager.deleteTimeStamp()
            }
        }
    }

    fun addTask(project: Project, variant: SourceSet) {
        val variantName = variant.name
        if (variantName != SOURCE_SETS_TEST) {
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_RELEASE, PACKAGING_JAR_CONSTANT, variantName), project)
            createTask(PublishJarExperimentalTask(), variant, getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING_JAR_CONSTANT, variantName), project)
            createTask(PublishJarLocalTask(), variant, getTaskName(TASK_TYPE_LOCAL, PACKAGING_JAR_CONSTANT, variantName), project)
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING_JAR_CONSTANT, variantName), project)
            createTask(PublishJarPublicReleaseTask(), variant, getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING_JAR_CONSTANT, variantName), project)
        }

        if (variantName == SOURCE_SETS_DEFAULT) {
            // If release, create mirror tasks without the flavor name
            // "Release" Task name maintained for retrocompatibility
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_RELEASE, PACKAGING_JAR_CONSTANT), project)
            createTask(PublishJarExperimentalTask(), variant, getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING_JAR_CONSTANT), project)
            createTask(PublishJarLocalTask(), variant, getTaskName(TASK_TYPE_LOCAL, PACKAGING_JAR_CONSTANT), project)
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING_JAR_CONSTANT), project)
            createTask(PublishJarPublicReleaseTask(), variant, getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING_JAR_CONSTANT), project)

            // And also mirror them without the Jar suffix too
            // "Release" Task name maintained for retrocompatibility
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_RELEASE), project)
            createTask(PublishJarExperimentalTask(), variant, getTaskName(TASK_TYPE_EXPERIMENTAL), project)
            createTask(PublishJarLocalTask(), variant, getTaskName(TASK_TYPE_LOCAL), project)
            createTask(PublishJarPrivateReleaseTask(), variant, getTaskName(TASK_TYPE_PRIVATE_RELEASE), project)
            createTask(PublishJarPublicReleaseTask(), variant, getTaskName(TASK_TYPE_PUBLIC_RELEASE), project)
        }
    }
}
