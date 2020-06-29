package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    @Override
    TaskProvider<Task> register(PublishTask.Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, flavorVersion("LOCAL-${project.version}-${getTimestamp()}", builder.variant))

        TaskProvider<Task> task

        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                doLast {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(project.name, builder.taskName, project.version as String)}")
                }
                group = TASK_GROUP

                dependsOn getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)
                finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
            }
        }
        createMavenPublication()

        return task
    }
}
