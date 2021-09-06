package com.mercadolibre.android.gradle.base.publish


import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishJarReleaseTask extends PublishJarTask {

    protected String repositoryName

    protected PublishJarReleaseTask(String repositoryName) {
        this.repositoryName = repositoryName
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

                doLast {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(project.name, task.name, project.version as String)}")
                }

                dependsOn "jar", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy "publish${taskName.capitalize()}PublicationTo${repositoryName}Repository"
            }
        }
        createMavenPublication()
        return task
    }
}

class PublishJarPrivateReleaseTask extends PublishJarReleaseTask {
    PublishJarPrivateReleaseTask() {
        super("AndroidRelease")
    }
}

class PublishJarPublicReleaseTask extends PublishJarReleaseTask {
    PublishJarPublicReleaseTask() {
        super("AndroidPublic")
    }
}