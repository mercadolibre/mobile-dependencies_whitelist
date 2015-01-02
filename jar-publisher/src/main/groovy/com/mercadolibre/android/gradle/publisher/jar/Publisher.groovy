package com.mercadolibre.android.gradle.publisher.jar

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublisherPlugin implements Plugin<Project> {

    void apply(Project project) {

        def task = project.tasks.create 'publishJar'
        task.description 'Publishes Jar :)'

        task.doLast {
            project.logger.error('Publishing Jar :)')
        }
    }
}