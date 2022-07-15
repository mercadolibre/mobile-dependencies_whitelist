package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.sub_classes

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain.PublishAarTask
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider.Companion.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider.Companion.PUBLIC_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LOCAL_SUBFIX_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_MAVEN_LOCAL
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

class PublishAarLocalTask : PublishAarTask() {

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

class PublishAarPublicReleaseTask : PublishAarReleaseTask(PUBLIC_RELEASES)
class PublishAarPrivateReleaseTask : PublishAarReleaseTask(INTERNAL_RELEASES)
