package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.extensions.PublisherPluginExtension
import com.mercadolibre.android.gradle.base.factories.PomFactory
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Created by mfeldsztejn on 5/26/17.
 */
class PublishableModule extends Module {

    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    public static final String TYPE_JAR = 'jar'
    public static final String TYPE_AAR = 'aar'

    public static final String ANDROID_LIBRARY_PLUGIN_ID = "com.android.library"

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

            plugins.withType(JavaPlugin) {
                configure(project, TYPE_JAR)
            }

            plugins.withId(ANDROID_LIBRARY_PLUGIN_ID) {
                configure(project, TYPE_AAR)
            }
        }

        project.tasks['uploadArchives'].dependsOn.clear()
    }

    def configure(Project project, String type) {
        project.apply plugin: 'maven'
        project.apply plugin: 'com.jfrog.bintray'

        project.afterEvaluate {
            validatePublisherContainer(project)
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: "file://${System.properties['user.home']}/.m2/repository")
                        PomFactory.create(new PomFactory.Builder().with {
                            it.project = project
                            it.packageType = type
                            return it
                        }).writeTo("build/poms/pom-default.xml")
                    }
                }
            }
        }

        Module module
        switch (type) {
            case TYPE_JAR:
                module = new JavaModule()
                break
            case TYPE_AAR:
            default:
                module = new AndroidLibraryModule()
        }
        module.configure(project)
    }


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
