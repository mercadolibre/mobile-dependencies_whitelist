package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.Task

/**
 * Created by saguilera on 7/23/17.
 */
class PublishJarLocalTask extends PublishJarTask {

    Task create(PublishTask.Builder builder) {
        super.create(builder)

        project.task(builder.taskName) {
            doFirst {
                VersionContainer.logVersion("${project.group}:${project.name}:${project.version}")
            }
            group = 'publishing'
            dependsOn "install"
        }
    }
}
