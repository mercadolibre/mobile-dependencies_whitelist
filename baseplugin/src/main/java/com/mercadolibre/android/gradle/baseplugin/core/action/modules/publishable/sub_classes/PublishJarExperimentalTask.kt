package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider.Companion.INTERNAL_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL_SUBFIX_TASK
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider

/**
 * PublishJarExperimentalTask is in charge of generating the Experimental Jar publications.
 */
class PublishJarExperimentalTask : PublishJarTask() {

    /**
     * This method is in charge of generating the necessary tasks to publish a module and the task that publishes the module.
     */
    override fun register(project: Project, variant: SourceSet, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            "${PUBLISHING_EXPERIMENTAL_SUBFIX_TASK}${project.version}-${getTimestamp()}",
            versionContainer,
            getListOfDependsOn(),
            INTERNAL_EXPERIMENTAL,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}
