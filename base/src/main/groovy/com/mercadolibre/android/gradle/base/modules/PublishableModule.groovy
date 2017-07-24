package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

/**
 * Created by mfeldsztejn on 5/26/17.
 */
abstract class PublishableModule extends Module {

    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    @Override
    void configure(Project project) {
        project.with {
            apply plugin: MavenPublishPlugin
            apply plugin: MavenPlugin
            apply plugin: 'com.jfrog.bintray'

            configurations {
                archives {
                    extendsFrom project.configurations.default
                }
            }
        }

        createGetProjectVersionTask(project)
    }

    /**
     * Creates the "getProjectVersion" task.
     */
    private void createGetProjectVersionTask(Project project) {
        def task = project.tasks.create TASK_GET_PROJECT_VERSION
        task.setDescription('Gets project version')

        task.doLast {
            def projectVersion = project.version

            def fileName = "project.version"
            def folder = new File('build')
            if (!folder.exists()) {
                folder.mkdirs()
            }

            def inputFile = new File("${folder}/${fileName}")
            inputFile.write("version: ${projectVersion}")
            println "See '${folder}/${fileName}' file"
        }
    }

}
