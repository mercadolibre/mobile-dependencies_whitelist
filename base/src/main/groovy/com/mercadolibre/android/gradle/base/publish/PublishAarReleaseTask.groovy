package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

abstract class PublishAarReleaseTask extends PublishAarTask {

    @Override
    TaskProvider<Task> register(PublishTask.Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, flavorVersion(project.version as String, builder.variant))

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                doFirst {
                    BintrayConfiguration.setBintrayConfig(getBintrayConfiguration())
                }
                group = TASK_GROUP

                dependsOn getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)
                finalizedBy 'bintrayUpload'
            }
        }
        createMavenPublication()

        return task
    }

    abstract BintrayConfiguration.Builder getBintrayConfiguration()
}
