package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarAlphaTask extends PublishJarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        VersionContainer.put(project.name, builder.taskName, "${project.version}-ALPHA-${getTimestamp()}")

        Task task
        if (project.tasks.findByName(builder.taskName)) {
            task = project.tasks."$taskName"
        } else {
            task = project.tasks.create(builder.taskName) {
                doFirst {
                    BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                        project = this.project
                        bintrayRepository = BINTRAY_RELEASE_REPOSITORY
                        publicationName = this.taskName
                        publicationPackaging = JavaPublishableModule.PACKAGING
                        publicationType = 'Alpha'
                        publish = true
                        return it
                    })
                }
                group = TASK_GROUP

                dependsOn "jar", "${variant.name}SourcesJar", "${variant.name}JavadocJar"
                finalizedBy 'bintrayUpload'
            }
        }

        createMavenPublication()
        return task
    }

}
