package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.PublishableModule
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishAarReleaseTask extends PublishAarTask {

    protected PublishableModule.Repository repository

    protected PublishAarReleaseTask(PublishableModule.Repository repository) {
        this.repository = repository
    }

    @Override
    TaskProvider<Task> register(Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, flavorVersion(project.version as String, builder.variant))

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                group = TASK_GROUP

                doLast {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(project.name, task.name, project.version as String)}")
                }

                dependsOn getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)
                finalizedBy "publish${taskName.capitalize()}PublicationTo${repository.name}Repository"
            }
        }
        createMavenPublication()

        return task
    }
}

class PublishAarPrivateReleaseTask extends PublishAarReleaseTask {
    PublishAarPrivateReleaseTask() {
        super(PublishableModule.Repository.ANDROID_RELEASES)
    }
}

class PublishAarPublicReleaseTask extends PublishAarReleaseTask {
    PublishAarPublicReleaseTask() {
        super(PublishableModule.Repository.ANDROID_PUBLIC)
    }
}
