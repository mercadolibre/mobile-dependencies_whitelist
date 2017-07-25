package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

import java.util.concurrent.atomic.AtomicReference

/**
 * Robolectric module in charge of setting up tasks for easy testing with robolectric dependencies
 *
 * Created by saguilera on 7/22/17.
 */
class RobolectricModule implements Module {

    private static final String PROJECT_TEST_PROPERTIES_FILE = "test-project.properties"
    private static final String PROJECT_PROPERTIES_FILE = "project.properties"

    protected  Project project

    protected final List variants = new ArrayList()

    @Override
    void configure(Project project) {
        this.project = project;

        // We have to store it since android property doesnt resolve lazily, using `all` we force the resolution
        // (Its in their docs)
        if (project.android.hasProperty('libraryVariants')) {
            project.android.libraryVariants.all { variants.add(it) }
        } else {
            project.android.applicationVariants.all { variants.add(it) }
        }

        createRobolectricFilesTask()
        createCopyManifestTask()
        createCleanRobolectricFilesTask()
    }

    private void hookToTestTasks(Task task, def variant = null) {
        def hook = { innerVariant ->
            def testTask = project.tasks.findByName("test${innerVariant.name()}UnitTest")
            if (testTask) {
                testTask.dependsOn(task)
            }
        }

        if (variant) {
            hook(variant)
        } else {
            variants.each { innerVariant -> hook(innerVariant) }
        }
    }

    /**
     * Creates Robolectric clean tasks
     */
    private void createCleanRobolectricFilesTask() {
        project.task('cleanRobolectricFiles') {
            description "Creates $PROJECT_TEST_PROPERTIES_FILE file necessary for Robolectric unit testing."
            outputs.upToDateWhen { !project.file("src/main/$PROJECT_TEST_PROPERTIES_FILE").exists() }
            project.tasks.clean.dependsOn it
            doLast {
                File file = project.file("src/main/$PROJECT_TEST_PROPERTIES_FILE")
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new GradleException("Cannot delete $PROJECT_TEST_PROPERTIES_FILE file. Check if some process is using it and close it.")
                    }
                }

                File projectFile = project.file("src/main/$PROJECT_PROPERTIES_FILE")
                if (projectFile.exists()) {
                    if (!projectFile.delete()) {
                        throw new GradleException("Cannot delete $PROJECT_PROPERTIES_FILE file. Check if some process is using it and close it.")
                    }
                }
            }
        }
    }

    /**
     * Copies files from ${buildDir}/intermediates/bundles/${buildType}/AndroidManifest.xml to ${buildDir}/intermediates/manifests/full/${buildType}/AndroidManifest.xml
     * this is a fix to help the transition to Android Gradle 2.2.0 with Robolectric < 3.1.1
     */
    private void createCopyManifestTask() {
        variants.each { variant ->
            project.task("copy${variant.name.capitalize()}AndroidManifestTask", type: Copy) { Copy task ->
                task.from "${project.buildDir}/intermediates/bundles/${variant.name}/AndroidManifest.xml"
                task.into "${project.buildDir}/intermediates/manifests/full/${variant.name}"

                task.dependsOn("process${variant.name.capitalize()}Manifest")

                hookToTestTasks(task)
            }
        }
    }

    /**
     * Creates Robolectric tasks necessary for unit testing
     */
    private void createRobolectricFilesTask() {
        project.task('createRobolectricFiles') { Task task ->
            description "Creates $PROJECT_TEST_PROPERTIES_FILE file necessary for Robolectric unit testing."
            doLast {
                File file = project.file("src/main/$PROJECT_TEST_PROPERTIES_FILE")
                file.createNewFile()

                project.file("src/main/$PROJECT_PROPERTIES_FILE").createNewFile()

                File[] tree = project.file("${project.buildDir}/intermediates/exploded-aar").listFiles()

                def path = "${project.rootProject.buildDir}/intermediates/exploded-aar/"
                def libCounter = new AtomicReference<Integer>()
                libCounter.set(new Integer(1))

                tree.each { File tmpFile ->
                    addDirToFile(file, tmpFile, path, libCounter)
                }
            }
            outputs.upToDateWhen { project.file("src/main/$PROJECT_TEST_PROPERTIES_FILE").exists() }

            hookToTestTasks(task)
        }
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

        def hasFiles = !(tree.findAll { File file -> !file.isDirectory() }.isEmpty())

        if (hasFiles) {
            roboFile.append("android.library.reference.${dirCounter.get().intValue()}=${path}${directory.name}\n")
            dirCounter.set(new Integer(dirCounter.get().intValue() + 1))
        } else {
            path += directory.name
            path += Character.LINE_SEPARATOR
            tree.each { File file -> addDirToFile(roboFile, file, path, dirCounter) }
        }
    }

}
