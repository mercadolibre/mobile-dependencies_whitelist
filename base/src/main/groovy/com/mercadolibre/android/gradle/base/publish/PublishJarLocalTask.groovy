package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarLocalTask extends PublishJarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(project.name, builder.taskName, "LOCAL-${project.version}-${getTimestamp()}")

        Task task
        if (project.tasks.findByName(builder.taskName)) {
            task = project.tasks."$taskName"
        } else {
            task = project.tasks.create(builder.taskName) {
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
