package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarTask
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLIC_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL_SUBFIX_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_MAVEN_LOCAL
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * PublishAarLocalTask is in charge of generating the Local Aar publications.
 */
class PublishAarLocalTask : PublishAarTask() {

    /**
     * This method is in charge of generating the necessary tasks to publish a module and the task that publishes the module.
     */
    override fun register(project: Project, variant: BaseVariant, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            flavorVersion("${PUBLISHING_LOCAL_SUBFIX_TASK}${project.version}-${TimeStampManager.getOrCreateTimeStamp()}", variant),
            versionContainer,
            listOf(getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)),
            PUBLISHING_MAVEN_LOCAL,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}

/**
 * PublishAarLocalTask is in charge of generating the Public Releleases Aar publications.
 */
class PublishAarPublicReleaseTask : PublishAarReleaseTask(PUBLIC_RELEASES)

/**
 * PublishAarLocalTask is in charge of generating the Private Releleases Aar publications.
 */
class PublishAarPrivateReleaseTask : PublishAarReleaseTask(INTERNAL_RELEASES)
