package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarExperimentalTask extends PublishAarTask {

    private String experimentalVersion

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        experimentalVersion = "EXPERIMENTAL-${project.publisher.version}-${getTimestamp()}"

        createMavenPublication()

        project.task(builder.taskName) {
            doFirst {
                BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                    project = this.project
                    bintrayRepository = BINTRAY_EXPERIMENTAL_REPOSITORY
                    version = version()
                    publicationName = this.taskName
                    return it
                })
            }

            dependsOn "check", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
            finalizedBy 'bintrayUpload'
        }
    }

    @Override
    protected String version() {
        return experimentalVersion
    }
}
