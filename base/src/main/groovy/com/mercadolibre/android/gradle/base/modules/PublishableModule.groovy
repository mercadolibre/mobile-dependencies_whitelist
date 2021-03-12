package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.ProjectRepositoryConfiguration
import com.mercadolibre.android.gradle.base.publish.Repository
import com.mercadolibre.android.gradle.base.publish.RepositoryProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskProvider

/**
 * Abstract module in charge of publishing archives into maven/bintray/whatever
 *
 * Created by saguilera on 7/22/17.
 */
abstract class PublishableModule implements Module {

    protected static final String TASK_TYPE_RELEASE = 'Release'
    protected static final String TASK_TYPE_EXPERIMENTAL = 'Experimental'
    protected static final String TASK_TYPE_LOCAL = 'Local'
    protected static final String TASK_TYPE_PUBLIC_RELEASE = 'PublicRelease'
    protected static final String TASK_TYPE_PRIVATE_RELEASE = 'PrivateRelease'

    private static final String TASK_GET_PROJECT_VERSION = "getProjectVersion"

    @Override
    void configure(Project project) {
        project.with {
            apply plugin: MavenPublishPlugin

            configurations {
                archives {
                    extendsFrom project.configurations.default
                }
            }
        }

        List<Repository> repositories = RepositoryProvider.getRepositories()
        ProjectRepositoryConfiguration.setupPublishingRepositories(project, repositories)

        createGetProjectVersionTask(project)
    }

    /**
     * Creates the "getProjectVersion" task.
     */
    private static void createGetProjectVersionTask(Project project) {
        TaskProvider<Task> task = project.tasks.register(TASK_GET_PROJECT_VERSION)
        task.configure { Task it ->
            it.setDescription('Gets project version')

            it.doLast {
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

    protected static String getTaskName(String type, String packaging = '', String variantName = '') {
        return "publish${packaging.capitalize()}${type}${variantName.capitalize()}"
    }
}
