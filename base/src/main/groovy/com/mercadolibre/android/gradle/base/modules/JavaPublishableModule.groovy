package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.publish.*
import org.gradle.api.Project

/**
 * Java publishing module in charge of creating tasks for each available sourceSet, and letting it publish it to bintray
 * or mavenLocal
 *
 * Currently it will create publish tasks for RELEASE / EXPERIMENTAL / LOCAL
 *
 * Created by saguilera on 7/21/17.
 */
class JavaPublishableModule extends PublishableModule {

    private static final String SOURCE_SETS_TEST = 'test'
    private static final String SOURCE_SETS_DEFAULT = 'main'

    public static final String PACKAGING = 'Jar'

    private Project project

    @Override
    void configure(Project project) {
        super.configure(project)

        this.project = project

        createTasks()
    }

    protected void createTask(PublishJarTask task, def libraryVariant, String theTaskName) {
        task.register(new PublishTask.Builder().with {
            project = this.project
            variant = libraryVariant
            taskName = theTaskName
            return it
        })
    }

    private void createTasks() {
        // JAR projects dont have local publishes since gradle already provides
        // the `install` task for it
        project.sourceSets.each {
            if (it.name != SOURCE_SETS_TEST) {
                String variantName = it.name
                createTask(new PublishJarPrivateReleaseTask(), it,
                        getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING, variantName))
                createTask(new PublishJarExperimentalTask(), it,
                        getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING, variantName))
                createTask(new PublishJarLocalTask(), it,
                        getTaskName(TASK_TYPE_LOCAL, PACKAGING, variantName))
                createTask(new PublishJarPublicReleaseTask(), it,
                        getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING, variantName))
            }

            if (it.name == SOURCE_SETS_DEFAULT) {
                // If release, create mirror tasks without the flavor name
                // 'Release' Task name maintained for retrocompatibility
                createTask(new PublishJarPrivateReleaseTask(), it, getTaskName(TASK_TYPE_RELEASE, PACKAGING))
                createTask(new PublishJarExperimentalTask(), it, getTaskName(TASK_TYPE_EXPERIMENTAL, PACKAGING))
                createTask(new PublishJarLocalTask(), it, getTaskName(TASK_TYPE_LOCAL, PACKAGING))
                createTask(new PublishJarPrivateReleaseTask(), it, getTaskName(TASK_TYPE_PRIVATE_RELEASE, PACKAGING))
                createTask(new PublishJarPublicReleaseTask(), it, getTaskName(TASK_TYPE_PUBLIC_RELEASE, PACKAGING))

                // And also mirror them without the Jar suffix too
                // 'Release' Task name maintained for retrocompatibility
                createTask(new PublishJarPrivateReleaseTask(), it, getTaskName(TASK_TYPE_RELEASE))
                createTask(new PublishJarExperimentalTask(), it, getTaskName(TASK_TYPE_EXPERIMENTAL))
                createTask(new PublishJarLocalTask(), it, getTaskName(TASK_TYPE_LOCAL))
                createTask(new PublishJarPrivateReleaseTask(), it, getTaskName(TASK_TYPE_PRIVATE_RELEASE))
                createTask(new PublishJarPublicReleaseTask(), it, getTaskName(TASK_TYPE_PUBLIC_RELEASE))
            }
        }
    }

}
