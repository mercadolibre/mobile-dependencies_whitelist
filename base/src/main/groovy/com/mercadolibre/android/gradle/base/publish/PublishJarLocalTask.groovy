package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarLocalTask extends PublishJarTask {

    @Override
    TaskProvider<Task> register(Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, "LOCAL-${project.version}-${getTimestamp()}")

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasksnamed(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                doFirst {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(project.name, builder.taskName, project.version as String)}")
                }
                group = TASK_GROUP

                dependsOn "jar", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
            }
        }
        createMavenPublication()

        return task
    }
}
