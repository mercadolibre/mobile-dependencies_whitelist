package com.mercadolibre.android.gradle.base.publish

import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarLocalTask extends PublishAarTask {

    private String localVersion

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        localVersion = "${project.publisher.version}-LOCAL-${getTimestamp()}"

        createMavenPublication()

        project.task(builder.taskName) {
            doFirst {
                project.version = version()
            }
            dependsOn "check", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
            finalizedBy "publish${taskName.capitalize()}PublicationToMavenLocal"
        }
    }

    @Override
    protected String version() {
        return localVersion
    }
}
