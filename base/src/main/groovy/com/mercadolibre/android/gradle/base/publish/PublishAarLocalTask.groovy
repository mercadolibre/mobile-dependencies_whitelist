package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(builder.taskName, flavorVersion(project.version as String, builder.variant))

        createMavenPublication()

        if (project.tasks.findByName(builder.taskName)) {
            return project.tasks."$taskName"
        } else {
            return project.task(builder.taskName) {
                dependsOn "bundle${variant.name.capitalize()}", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                doLast {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(builder.taskName, project.version as String)}")
                }
                group = TASK_GROUP
                finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
            }
        }
    }
}
