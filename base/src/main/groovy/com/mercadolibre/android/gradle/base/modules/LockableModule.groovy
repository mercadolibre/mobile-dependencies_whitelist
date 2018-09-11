package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ComponentSelection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction

/**
 * Module that is in charge of managing the locking of dynamic dependencies into static ones
 *
 * Created by saguilera on 7/22/17.
 */
class LockableModule implements Module {

    private static final String TASK_LOCK_VERSIONS = "lockVersions"
    private static final String TASK_UPDATE_LOCKS = "updateLocks"

    private static final String DEPENDENCIES_TASK = "dependencies"

    private static final String VERSION_ALPHA = "ALPHA"

    @Override
    void configure(Project project) {
        project.afterEvaluate {
            // Add a dependency filter so that it wont lock local dependencies
            project.configurations.all {
                it.resolutionStrategy.activateDependencyLocking()
            }

            def tasks = project.gradle.startParameter.taskNames.toListString()
            if (tasks.contains(TASK_LOCK_VERSIONS) || tasks.contains(TASK_UPDATE_LOCKS)) {
                rejectAlphas(project)
            }

            createTask(project, TASK_LOCK_VERSIONS, { Project p ->
                return [p.gradle.startParameter.writeDependencyLocks, "--write-locks"]
            })

            createTask(project, TASK_UPDATE_LOCKS, { Project p ->
                return [!p.gradle.startParameter.getLockedDependenciesToUpdate().isEmpty(), "--update-locks"]
            })

            project.tasks.create("modifyLocks", UpdateLockTask)
            project.tasks.create("deleteLocks", DeleteLocksTask)
        }
    }

    def rejectAlphas(Project project) {
        def localDeps = []
        project.rootProject.subprojects.each { localDeps.add("$it.group:$it.name") }
        project.configurations.all {
            if (it.state == Configuration.State.UNRESOLVED) {
                it.resolutionStrategy {
                    componentSelection.all { ComponentSelection selection ->
                        // If the version has an alpha and it's not me reject the version
                        // If it's me, we will change it later
                        String artifact = "${selection.candidate.group}:${selection.candidate.module}"
                        if (!localDeps.contains(artifact) &&
                                selection.candidate.version.contains(VERSION_ALPHA)) {
                            selection.reject("Bad version. We dont accept alphas on the lock stage.")
                        }
                    }
                }
            }
        }
    }

    def createTask(Project project, String name, def validate) {
        Task task = project.tasks.create(name)
        task.group = "locking"
        task.doFirst {
            def result = validate(project)
            if (!result[0]) {
                throw new IllegalArgumentException("Did you add the ${result[1]} flag?")
            }
        }
        task.dependsOn project.tasks.findByName(DEPENDENCIES_TASK)
        task.doLast {
            new File("${project.projectDir}/gradle/dependency-locks/").eachFile { File file ->
                boolean onlyHasComments = true
                file.eachLine {
                    onlyHasComments &= it.toString().startsWith("#")
                }
                if (onlyHasComments) {
                    file.delete()
                }
            }
        }
    }

}

class LocksTask extends DefaultTask {
    File getLocksFolder(){
        def locksFolder = new File("${project.projectDir}/gradle/dependency-locks/")

        if (!locksFolder.exists() || !locksFolder.isDirectory() || locksFolder.listFiles().length == 0) {
            throw new IllegalStateException("Did you create the locks?")
        }

        return locksFolder
    }
}

class UpdateLockTask extends LocksTask {
    UpdateLockTask() {
        group = "locking"
    }

    @TaskAction
    void update() {
        if (!project.hasProperty("modules")) {
            throw new IllegalArgumentException("Did you use the -Pmodules=\${modules} argument?")
        }

        def modules = new ArrayList<String>()
        def property = project.properties["modules"] as String
        if (property.contains(',')) {
            modules.addAll(property.split(','))
        } else {
            modules.add(property)
        }

        locksFolder.eachFile { File file ->
            def outLines = []
            file.eachLine { String line ->
                boolean matched = false
                if (line.contains(":")) {
                    def (fileGroup, fileName, fileVersion) = line.split(":")
                    // For each line check if it matches any module
                    modules.each { String module ->
                        def (moduleGroup, moduleName, moduleVersion) = module.split(":")
                        if (!moduleGroup || !moduleName || !moduleVersion) {
                            throw new IllegalArgumentException("$moduleGroup:$moduleName:$moduleVersion is an invalid parameter. Are you sure this is right?")
                        }
                        if (fileGroup =~ moduleGroup && fileName =~ moduleName) {
                            matched = true
                            outLines << "$fileGroup:$fileName:$moduleVersion"
                        }
                    }
                }
                if (!matched) {
                    outLines << line
                }
            }
            file.withWriter { out -> outLines.each { out.println it } }
        }
    }
}

class DeleteLocksTask extends LocksTask {
    @TaskAction
    def delete(){
        locksFolder.eachFile { File file ->
            if (file.name.toLowerCase() =~ "^.*\\.lockfile\$") {
                file.delete()
            }
        }
    }
}