package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * This class generates the Aar Release posts with help of TaskGenerator
 */
abstract class PublishAarReleaseTask(private val repositoryName: String) : PublishAarTask() {

    override fun register(project: Project, variant: BaseVariant, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            flavorVersion(project.version as String, variant),
            versionContainer,
            listOf(getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)),
            repositoryName,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}
