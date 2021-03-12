package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarExperimentalTask extends PublishJarTask {

    @Override
    TaskProvider<Task> register(Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, "EXPERIMENTAL-${project.version}-${getTimestamp()}")

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                group = TASK_GROUP

                dependsOn "jar", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy "publish${taskName.capitalize()}PublicationToAndroidExperimentalRepository"
            }
        }
        createMavenPublication()
        return task
    }
}
