package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(builder.taskName, "${project.version}-LOCAL-${getTimestamp()}")

        createMavenPublication()

        project.task(builder.taskName) {
            dependsOn "check", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
            finalizedBy "${taskName}PublicationToMavenLocal"
        }
    }
}
