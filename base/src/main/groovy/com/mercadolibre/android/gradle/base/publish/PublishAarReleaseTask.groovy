package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishAarReleaseTask extends PublishAarTask {

    protected String repositoryName

    protected PublishAarReleaseTask(String repositoryName) {
        this.repositoryName = repositoryName
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
                finalizedBy "publish${taskName.capitalize()}PublicationTo${repositoryName}Repository"
            }
        }
        createMavenPublication()

        return task
    }
}

class PublishAarPrivateReleaseTask extends PublishAarReleaseTask {
    PublishAarPrivateReleaseTask() {
        super("AndroidRelease")
    }
}

class PublishAarPublicReleaseTask extends PublishAarReleaseTask {
    PublishAarPublicReleaseTask() {
        super("AndroidPublic")
    }
}
