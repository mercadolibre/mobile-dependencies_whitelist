package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_MAVEN_LOCAL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_CONSTANT
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized

/**
 * The TaskGenerator class is responsible for generating the publication tasks for each of the options that exist.
 *
 * - Publish Jar or Aar
 * - Release
 * - Local
 * - Experimental
 */
class TaskGenerator(
    taskName: String,
    version: String,
    private val versionContainer: VersionContainer,
    list: List<String>,
    repositoryName: String,
    private val project: Project
) {

    var task: TaskProvider<Task>

    init {
        versionContainer.put(project.name, taskName, version)
        task =
            if (project.tasks.names.contains(taskName)) {
                project.tasks.named(taskName)
            } else {
                project.tasks.register(taskName).apply {
                    configure {
                        group = PUBLISHING_GROUP

                        doLast {
                            logVersion(name)
                        }

                        dependsOn(list)

                        var finalizedTask = "$PUBLISH_CONSTANT${taskName.capitalized()}PublicationTo$repositoryName"

                        if (!repositoryName.contains(PUBLISHING_MAVEN_LOCAL)) {
                            finalizedTask += "Repository"
                        }

                        finalizedBy(finalizedTask)
                    }
                }
            }
    }

    fun logVersion(taskName: String) {
        versionContainer.logVersion(
            "${project.group}:${project.name}:" +
                versionContainer.get(
                    project.name,
                    taskName,
                    project.version as String
                )
        )
    }
}
