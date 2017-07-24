package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarReleaseTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        createMavenPublication()

        project.task(builder.taskName) {
            doFirst {
                BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                    project = this.project
                    bintrayRepository = BINTRAY_RELEASE_REPOSITORY
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
        return project.publisher.version
    }
}
