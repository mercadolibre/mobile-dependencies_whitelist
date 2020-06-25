package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarExperimentalTask extends PublishJarTask {

    @Override
    TaskProvider<Task> register(PublishTask.Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, "EXPERIMENTAL-${project.version}-${getTimestamp()}")

        TaskProvider<Task> task
        if (project.tasks.names.contains(builder.taskName)) {
            task = project.tasks.named(builder.taskName)
        } else {
            task = project.tasks.register(builder.taskName)
            task.configure {
                doFirst {
                    BintrayConfiguration.setBintrayConfig(new BintrayConfiguration.Builder().with {
                        project = this.project
                        bintrayRepository = BINTRAY_EXPERIMENTAL_REPOSITORY
                        publicationName = this.taskName
                        publicationPackaging = JavaPublishableModule.PACKAGING
                        publicationType = 'Experimental'
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
