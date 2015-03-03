package com.mercadolibre.android.gradle.library.robolectric

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task

import java.util.concurrent.atomic.AtomicReference

/**
 * Robolectric Tasks
 *
 * Created by ngiagnoni on 2/27/15.
 */
class RobolectricTaskManager {
    /**
     * The Project
     */
    private Project project;

    public void apply(Project project){
        this.project = project;

        configureRobolectricTasks()
        configureExampleApp()
    }

    private void configureRobolectricTasks(){
        createRobolectricFilesTask()
        createCleanRobolectricFilesTask()
    }

    private void configureExampleApp() {
        def exampleApp = project.getProperties().get("exampleApp")
        if (exampleApp != null){
            project.logger.warn("INFO: Property 'exampleApp' loaded. Value is \"${exampleApp}\" in \"${project.name}\"")
            project.android.sourceSets.test.java.srcDirs += "../${exampleApp}/build/generated/source/r/debug"
        } else {
            project.logger.warn("WARNING: Property 'exampleApp' is needed by the Robolectric plugin to work properly. Please define it in the gradle.properties of \"${project.name}\"")
        }
    }

    private void hookToCleanTask() {
        def cleanTask = project.tasks.findByName("clean")

        cleanTask.finalizedBy "cleanRobolectricFiles"
    }

    private void createCleanRobolectricFilesTask() {
        def task = project.tasks.create 'cleanRobolectricFiles'
        task.setDescription('Creates \"test-project.properties\" file necessary for Robolectric unit testing.')

        task.doLast {
            File file = project.file("src/main/test-project.properties")
            if (file.exists()){
                if (!file.delete()){
                    throw new GradleException("Cannot delete \"test-project.properties\" file. Check if some process is using it and close it.")
                }
            }

            File projectFile = project.file("src/main/project.properties")
            if (projectFile.exists()){
                if (!projectFile.delete()){
                    throw new GradleException("Cannot delete \"project.properties\" file. Check if some process is using it and close it.")
                }
            }
        }

        task.getOutputs().upToDateWhen {
            File file = project.file("src/main/test-project.properties")
            return !file.exists()
        }

        hookToCleanTask()
    }

    private void createRobolectricFilesTask() {
        def task = project.tasks.create 'createRobolectricFiles'
        task.setDescription('Creates \"test-project.properties\" file necessary for Robolectric unit testing.')

        task.doLast {
            File file = project.file("src/main/test-project.properties")
            file.createNewFile()

            project.file("src/main/project.properties").createNewFile()

            File[] tree = project.file("build/intermediates/exploded-aar").listFiles()

            def path = "../../build/intermediates/exploded-aar/"
            def libCounter = new AtomicReference<Integer>()
            libCounter.set(new Integer(1))

            tree.each {File tmpFile ->
                addDirToFile(file, tmpFile, path, libCounter)
            }
        }

        task.getOutputs().upToDateWhen {
            File file = project.file("src/main/test-project.properties")
            return file.exists()
        }
    }

    private void addDirToFile(File roboFile, File directory, String path, AtomicReference<Integer> dirCounter) {
        File[] tree = directory.listFiles()

        def hasFiles = false

        tree.each { File file ->
            if (!file.isDirectory())
                hasFiles = true
        }

        if (hasFiles) {
            roboFile.append("android.library.reference.${dirCounter.get().intValue()}=${path}${directory.name}\n")
            def oldValue = dirCounter.get().intValue()
            dirCounter.set(new Integer(oldValue + 1))
        } else {
            path += directory.name
            path += "/"
            tree.each { File file ->
                addDirToFile(roboFile, file, path, dirCounter)
            }
        }
    }

    public Task retrieveRobolecticFilesTask (){
        return project.tasks.findByName("createRobolectricFiles")
    }
}
