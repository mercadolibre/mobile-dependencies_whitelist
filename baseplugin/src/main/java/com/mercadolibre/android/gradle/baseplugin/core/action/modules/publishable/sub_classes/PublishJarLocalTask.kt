package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishJarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider.Companion.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider.Companion.PUBLIC_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL_SUBFIX_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_MAVEN_LOCAL
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider

/**
 * PublishJarLocalTask is in charge of generating the Local Jar publications.
 */
class PublishJarLocalTask : PublishJarTask() {

    override fun register(project: Project, variant: SourceSet, taskName: String): TaskProvider<Task> {
        this.project = project
        this.variant = variant
        this.taskName = taskName

        val taskGenerator = TaskGenerator(
            taskName,
            "${PUBLISHING_LOCAL_SUBFIX_TASK}${project.version}-${getTimestamp()}",
            versionContainer,
            getListOfDependsOn(),
            PUBLISHING_MAVEN_LOCAL,
            project
        )

        createMavenPublication()

        return taskGenerator.task
    }
}

class PublishJarPrivateReleaseTask : PublishJarReleaseTask(INTERNAL_RELEASES)
class PublishJarPublicReleaseTask : PublishJarReleaseTask(PUBLIC_RELEASES)
