package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.modules.PublishableModule
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarExperimentalTask extends PublishAarTask {

    @Override
    TaskProvider<Task> register(Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, flavorVersion("EXPERIMENTAL-${project.version}-${getTimestamp()}", builder.variant))

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
                finalizedBy "publish${taskName.capitalize()}PublicationTo${PublishableModule.Repository.ANDROID_EXPERIMENTAL.name}Repository"
            }
        }

        createMavenPublication()

        return task
    }
}
