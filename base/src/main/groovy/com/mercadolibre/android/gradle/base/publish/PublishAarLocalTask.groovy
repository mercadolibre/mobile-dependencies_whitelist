package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(builder.taskName, "${project.version}")

        createMavenPublication()

        project.task(builder.taskName) {
            dependsOn "${variant.name}SourcesJar", "${variant.name}JavadocJar"
            doLast {
                VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(builder.taskName, project.version as String)}")
            }
            group = 'publishing'
            finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
        }
    }
}
