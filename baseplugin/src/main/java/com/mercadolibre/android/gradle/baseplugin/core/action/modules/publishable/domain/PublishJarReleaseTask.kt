package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider

/**
 * This class generates the Jar Release posts with help of TaskGenerator and PublishManager
 */
abstract class PublishJarReleaseTask(private val repositoryName: String) : PublishJarTask() {

    override fun register(project: Project, variant: SourceSet, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName
        versionContainer.put(project.name, taskName, project.version as String)

        val taskGenerator = TaskGenerator(
            taskName,
            project.version as String,
            versionContainer,
            getListOfDependsOn(),
            repositoryName,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}
