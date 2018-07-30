package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.artifacts.ComponentSelection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

/**
 * Module that is in charge of managing the locking of dynamic dependencies into static ones
 *
 * Created by saguilera on 7/22/17.
 */
class LockableModule implements Module {

    private static final String TASK_LOCK_VERSIONS = "lockVersions"

    private static final String NEBULA_LOCK_PLUGIN_NAME = 'nebula.dependency-lock'
    private static final String NEBULA_LOCK_TASKS_NAME_MATCHER = "lock"
    private static final String[] NEBULA_LOCK_TASKS = [
            "generateLock",
            "saveLock"
    ]

    private static final String VERSION_ALPHA = "ALPHA"

    @Override
    void configure(Project project) {
        project.apply plugin: NEBULA_LOCK_PLUGIN_NAME

        def taskDescription = 'Locks the compiled project with the current versions of its dependencies to keep them in future assembles'
        project.afterEvaluate {
            // Add a strategy to filter all ALPHA versions when running a lock task
            // this way we will only lock to release versions (or experimentals if explicitly added)
            if (project.gradle.startParameter.taskNames.toListString().toLowerCase().contains(NEBULA_LOCK_TASKS_NAME_MATCHER)) {
                project.configurations.all {
                    if (it.state == Configuration.State.UNRESOLVED) {
                        resolutionStrategy {
                            componentSelection.all { ComponentSelection selection ->
                                // If the version has an alpha and it's not me reject the version
                                // If it's me, we will change it later
                                if (!artifactIsFromProject(project, selection.candidate) &&
                                        selection.candidate.version.contains(VERSION_ALPHA)) {
                                    selection.reject("Bad version. We dont accept alphas on the lock stage.")
                                }
                            }
                        }
                    }
                }
            }

            // Add a dependency filter so that it wont lock local dependencies
            def localDeps = []
            project.rootProject.subprojects.each { localDeps.add("$it.group:$it.name") }

            project.dependencyLock.dependencyFilter { String group, String name, String version ->
                return !localDeps.contains("$group:$name")
            }

            // Create a task that wraps the flow of the locking logic
            project.task(TASK_LOCK_VERSIONS) {
                description taskDescription
                dependsOn NEBULA_LOCK_TASKS
            }
        }
    }

    static boolean artifactIsFromProject(Project project, ModuleComponentIdentifier dependency) {
        return project.subprojects.find { it.name == dependency.module && it.group == dependency.group } != null
    }

}
