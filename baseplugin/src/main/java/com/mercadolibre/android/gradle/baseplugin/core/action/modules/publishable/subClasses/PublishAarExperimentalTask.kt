package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarTask
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL_SUBFIX_TASK
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * PublishAarExperimentalTask is in charge of generating the Experimental Aar publications.
 */
class PublishAarExperimentalTask : PublishAarTask() {

    /**
     * This method is in charge of generating the necessary tasks to publish a module and the task that publishes the module.
     */
    override fun register(project: Project, variant: BaseVariant, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            flavorVersion("${PUBLISHING_EXPERIMENTAL_SUBFIX_TASK}${project.version}-${TimeStampManager.getOrCreateTimeStamp()}", variant),
            versionContainer,
            listOf(getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)),
            INTERNAL_EXPERIMENTAL,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}
