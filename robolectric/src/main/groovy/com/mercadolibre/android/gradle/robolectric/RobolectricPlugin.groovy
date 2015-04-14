package com.mercadolibre.android.gradle.robolectric

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicReference

/**
 * Created by ngiagnoni on 3/11/15.
 */
public class RobolectricPlugin implements Plugin<Project> {

    /**
     * The Project
     */
    private Project project;

    /**
     * Invoke this method using Gradle Project to apply Robolectric Tasks
     * @param project The Gradle project
     */
    @Override
    public void apply(Project project){
        this.project = project;

        configureRobolectricTasks()
        configureExampleApp()
    }

    /**
     * Create all Robolectric Tasks
     */
    private void configureRobolectricTasks(){
        if (project.android == null){
            throw new GradleException("You should apply \"android\" plugin to make this one work.")
        }

        createRobolectricFilesTask()
        createCleanRobolectricFilesTask()
    }

    /**
     * Configures android sourcets in unit testing tasks to obtain auto-generated classes to work
     */
    private void configureExampleApp() {
        def exampleApp = project.getProperties().get("exampleApp")
        if (exampleApp != null){
            project.logger.warn("INFO: Property 'exampleApp' loaded. Value is \"${exampleApp}\" in \"${project.name}\"")
            project.android.sourceSets.test.java.srcDirs += "../${exampleApp}/build/generated/source/r/debug"
        }
    }

    private void hookToTestTasks(){
        def variants = null;

        try {
            variants = project.android.applicationVariants
        } catch (Exception e) {
            variants = project.android.libraryVariants
        }

        variants.all { variant ->
            def taskName = "test${variant.flavorName.capitalize()}${variant.buildType.name.capitalize()}"
            def testTask = project.tasks.findByName(taskName)
            testTask.dependsOn("createRobolectricFiles")
        }
    }

    /**
     * Hooks Robolectric clean tasks to project' clean task
     */
    private void hookToCleanTask() {
        def cleanTask = project.tasks.findByName("clean")
        cleanTask.finalizedBy "cleanRobolectricFiles"
    }

    /**
     * Creates Robolectric clean tasks
     */
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

    /**
     * Creates Robolectric tasks necessary for unit testing
     */
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

        hookToTestTasks()
    }

    /**
     * Recursive method to scan library dependencies and add them to Robolectric file
     * @param roboFile Robolectric configuration file
     * @param directory Directory where search must start
     * @param path Accumulative path through recursive method
     * @param dirCounter Robolectric file dirs added counter
     */
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

}
