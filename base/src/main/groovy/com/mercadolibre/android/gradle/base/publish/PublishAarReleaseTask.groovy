package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarReleaseTask extends PublishAarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(builder.taskName, flavorVersion(project.version as String, builder.variant))

        createMavenPublication()

        if (project.tasks.findByName(builder.taskName)) {
            return project.tasks."$taskName"
        } else {
            return project.task(builder.taskName) {
                doFirst {
                    BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                        project = this.project
                        bintrayRepository = BINTRAY_RELEASE_REPOSITORY
                        publicationName = this.taskName
                        return it
                    })
                }
                group = TASK_GROUP

                dependsOn "bundle${variant.name.capitalize()}", "check", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy 'bintrayUpload'
            }
        }
    }

}
