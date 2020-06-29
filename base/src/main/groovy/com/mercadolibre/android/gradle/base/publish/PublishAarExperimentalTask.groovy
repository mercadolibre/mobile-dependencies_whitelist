package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by saguilera on 7/23/17.
 */
class PublishAarExperimentalTask extends PublishAarTask {

    @Override
    TaskProvider<Task> register(PublishTask.Builder builder) {
        super.register(builder)

        VersionContainer.put(project.name, builder.taskName, flavorVersion("EXPERIMENTAL-${project.version}-${getTimestamp()}", builder.variant))

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
                        publicationPackaging = AndroidLibraryPublishableModule.PACKAGING
                        publicationType = 'Experimental'
                        return it
                    })
                }
                group = TASK_GROUP

                dependsOn getBundleTaskName(project, variant), getSourcesJarTaskName(variant), getJavadocJarTask(variant)
                finalizedBy 'bintrayUpload'
            }
        }

        createMavenPublication()

        return task
    }

}
