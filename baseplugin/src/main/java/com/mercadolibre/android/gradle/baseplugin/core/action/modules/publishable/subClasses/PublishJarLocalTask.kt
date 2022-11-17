package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarTask
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLIC_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL_SUBFIX_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_MAVEN_LOCAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_GENERATOR
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider

/**
 * PublishJarLocalTask is in charge of generating the Local Jar publications.
 */
class PublishJarLocalTask : PublishJarTask() {

    /**
     * This method is in charge of generating the necessary tasks to publish a module and the task that publishes the module.
     */
    override fun register(project: Project, variant: SourceSet, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            "${PUBLISHING_LOCAL_SUBFIX_TASK}${project.version}-${TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR)}",
            versionContainer,
            getListOfDependsOn(),
            PUBLISHING_MAVEN_LOCAL,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}

/**
 * PublishJarLocalTask is in charge of generating the Private Releases Jar publications.
 */
class PublishJarPrivateReleaseTask : PublishJarReleaseTask(INTERNAL_RELEASES)

/**
 * PublishJarLocalTask is in charge of generating the Public Releases Jar publications.
 */
class PublishJarPublicReleaseTask : PublishJarReleaseTask(PUBLIC_RELEASES)
