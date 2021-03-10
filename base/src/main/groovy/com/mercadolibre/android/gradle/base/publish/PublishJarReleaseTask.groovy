package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.PublishableModule
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishJarReleaseTask extends PublishJarTask {

    protected PublishableModule.Repository repository

    protected PublishJarReleaseTask(PublishableModule.Repository repository) {
        this.repository = repository
    }

    @Override
    TaskProvider<Task> register(Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, project.version as String)

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                group = TASK_GROUP

                dependsOn "jar", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy "publish${taskName.capitalize()}PublicationTo${repository.name}Repository"
            }
        }
        createMavenPublication()
        return task
    }
}

class PublishJarPrivateReleaseTask extends PublishJarReleaseTask {
    PublishJarPrivateReleaseTask() {
        super(PublishableModule.Repository.ANDROID_RELEASES)
    }
}

class PublishJarPublicReleaseTask extends PublishJarReleaseTask {
    PublishJarPublicReleaseTask() {
        super(PublishableModule.Repository.ANDROID_PUBLIC)
    }
}