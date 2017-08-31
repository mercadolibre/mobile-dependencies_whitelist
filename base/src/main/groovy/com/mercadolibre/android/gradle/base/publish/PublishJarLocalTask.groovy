package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarLocalTask extends PublishJarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        if (project.tasks.findByName(builder.taskName)) {
            return project.tasks."$taskName"
        } else {
            return project.task(builder.taskName) {
                doFirst {
                    VersionContainer.logVersion("${project.group}:${project.name}:${project.version}")
                }
                group = TASK_GROUP
                dependsOn "publish${taskName.capitalize()}PublicationToMavenLocal"
            }
        }
    }
}
