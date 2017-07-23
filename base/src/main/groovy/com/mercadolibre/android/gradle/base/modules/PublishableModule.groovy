package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.extensions.PublisherPluginExtension
import com.mercadolibre.android.gradle.base.factories.PomFactory
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Created by mfeldsztejn on 5/26/17.
 */
abstract class PublishableModule extends Module {

    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    @Override
    void configure(Project project) {
        createGetProjectVersionTask(project)

        project.with {
            extensions.create('publisher', PublisherPluginExtension)

            configurations {
                archives {
                    extendsFrom project.configurations.default
                }
            }

            apply plugin: 'maven'
            apply plugin: 'com.jfrog.bintray'

            afterEvaluate {
                validatePublisherContainer(project)
                uploadArchives {
                    repositories {
                        mavenDeployer {
                            repository(url: "file://${System.properties['user.home']}/.m2/repository")
                            pom = PomFactory.create(new PomFactory.Builder().with {
                                it.project = project
                                it.packageType = packageType()
                                return it
                            })
                        }
                    }
                }
            }

            tasks['uploadArchives'].dependsOn.clear()
        }
    }

    protected abstract String packageType()

    /**
     * Validates that all the needed configuration is set within the 'publisher' container.
     */
    protected void validatePublisherContainer(Project project) {
        // Publisher container.
        PublisherPluginExtension publisherContainer = project.publisher
        if (!publisherContainer.groupId) {
            throw new GradleException("Property 'publisher.groupId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (!publisherContainer.artifactId) {
            throw new GradleException("Property 'publisher.artifactId' is needed by the Publisher plugin. Please define it in the build script.")
        }
        if (!publisherContainer.version) {
            throw new GradleException("Property 'publisher.version' is needed by the Publisher plugin. Please define it in the build script.")
        }

    }

    /**
     * Creates the "getProjectVersion" task.
     */
    private void createGetProjectVersionTask(Project project) {
        def task = project.tasks.create TASK_GET_PROJECT_VERSION
        task.setDescription('Gets project version')

        task.doLast {
            def projectVersion = project.publisher.version;

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
