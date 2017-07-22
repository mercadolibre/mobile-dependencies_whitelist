package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import java.util.concurrent.atomic.AtomicReference

/**
 * Created by saguilera on 7/22/17.
 */
class RobolectricModule extends Module {

    private Project project;

    @Override
    void configure(Project project) {
        this.project = project;
        configureRobolectricTasks()
    }

    /**
     * Create all Robolectric Tasks
     */
    private void configureRobolectricTasks() {
        createRobolectricFilesTask()
        createCopyManifestTask();
        createCleanRobolectricFilesTask()
    }

    private void hookToTestTasks(def task, def buildType=null) {
        def variants;

        try {
            variants = project.android.applicationVariants
        } catch (Exception ignored) {
            variants = project.android.libraryVariants
        }

        variants.all { variant ->
            // checks if the hook need to be only for a certain buildType
            if (buildType != null && buildType.name != variant.buildType.name) {
                return;
            }
            def taskName = "test${variant.flavorName.capitalize()}${variant.buildType.name.capitalize()}UnitTest"
            def testTask = project.tasks.findByName(taskName)

            testTask.dependsOn(task)
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
            if (file.exists()) {
                if (!file.delete()) {
                    throw new GradleException("Cannot delete \"test-project.properties\" file. Check if some process is using it and close it.")
                }
            }

            File projectFile = project.file("src/main/project.properties")
            if (projectFile.exists()) {
                if (!projectFile.delete()) {
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
     * Copies files from ${buildDir}/intermediates/bundles/${buildType}/AndroidManifest.xml to ${buildDir}/intermediates/manifests/full/${buildType}/AndroidManifest.xml
     * this is a fix for to help the transition to Android Gradle 2.2.0 with Robolectric < 3.1.1
     */
    private void createCopyManifestTask() {
        project.android.buildTypes.each { buildType ->
            def taskName = "copy${buildType.name.capitalize()}AndroidManifestTask"
            def task = project.task(taskName, type: Copy) { Copy t ->
                t.from "${project.buildDir}/intermediates/bundles/${buildType.name}/AndroidManifest.xml"
                t.into "${project.buildDir}/intermediates/manifests/full/${buildType.name}"
            }
            // depends on the task that generates the buildType Manifest
            task.dependsOn("process${buildType.name.capitalize()}Manifest")

            hookToTestTasks(task, buildType)
        }
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

            tree.each { File tmpFile ->
                addDirToFile(file, tmpFile, path, libCounter)
            }
        }

        task.getOutputs().upToDateWhen {
            File file = project.file("src/main/test-project.properties")
            return file.exists()
        }
        hookToTestTasks(task)
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
