package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarAlphaTask extends PublishJarTask {

    private String alphaVersion

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        alphaVersion = "${project.publisher.version}-ALPHA-${getTimestamp()}"

        createMavenPublication()

        project.task(builder.taskName) {
            doFirst {
                BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                    project = this.project
                    bintrayRepository = BINTRAY_RELEASE_REPOSITORY
                    version = alphaVersion
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
        return alphaVersion
    }

}
