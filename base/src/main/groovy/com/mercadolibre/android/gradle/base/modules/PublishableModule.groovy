package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskProvider
import org.omg.CORBA.RepositoryIdHelper

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

    enum Repository {
        ANDROID_PUBLIC("AndroidPublic"),
        ANDROID_RELEASES("AndroidReleases"),
        ANDROID_EXPERIMENTAL("AndroidExperimental")

        private String name

        Repository(String name) {
            this.name = name
        }

        String getName() {
            return name
        }
    }

    private static final Map<Repository, String> REPOSITORIES = new HashMap<Repository, String>() {
        {
            put(Repository.ANDROID_PUBLIC, "https://android-test.artifacts.furycloud.io/repository/internal")
            put(Repository.ANDROID_EXPERIMENTAL, "https://android-test.artifacts.furycloud.io/repository/internal")
            put(Repository.ANDROID_RELEASES, "https://android-test.artifacts.furycloud.io/repository/internal")
        }
    }

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

        setupPublishingRepositories(project)

        createGetProjectVersionTask(project)
    }

    private void setupPublishingRepositories(Project project) {
        REPOSITORIES.forEach({ repository, repUrl ->
            project.publishing.repositories.maven {
                name = repository.name
                url = repUrl
                credentials {
                    credentials {
                        username "mobile"
                        password "123456"
                    }
                }
            }
        })
    }

    /**
     * Creates the "getProjectVersion" task.
     */
    private void createGetProjectVersionTask(Project project) {
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

    protected String getTaskName(String type, String packaging = '', String variantName = '') {
        return "publish${packaging.capitalize()}${type}${variantName.capitalize()}"
    }

}
