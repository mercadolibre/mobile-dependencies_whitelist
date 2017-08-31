package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(builder.taskName, flavorVersion("LOCAL-${project.version}-${getTimestamp()}", builder.variant))

        createMavenPublication()

        if (project.tasks.findByName(builder.taskName)) {
            return project.tasks."$taskName"
        } else {
            return project.task(builder.taskName) {
                doLast {
                    VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(builder.taskName, project.version as String)}")
                }
                group = TASK_GROUP

                dependsOn "bundle${variant.name.capitalize()}", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
            }
        }
    }
}
