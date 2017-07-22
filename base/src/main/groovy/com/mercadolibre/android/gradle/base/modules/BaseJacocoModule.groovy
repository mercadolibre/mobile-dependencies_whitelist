package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Created by saguilera on 7/22/17.
 */
abstract class BaseJacocoModule extends Module {

    protected Project project

    @Override
    void configure(Project project) {
        this.project = project

        // We apply jacoco plugin allowing us to create Unit tests code coverage report
        project.apply plugin: 'jacoco'

        // Note that the following version of the JaCoCo tool is also at TestCoverage.groovy
        project.jacoco {
            toolVersion = "0.7.7.201606060606"
        }

        createCleanJacocoTask()
    }


    private void createCleanJacocoTask() {
        def task = project.tasks.create 'cleanJacocoFiles'
        task.setDescription('Clean all Jacoco related files.')

        task.doLast {
            File file = project.file("./jacoco.exec")
            if (!file.delete()) {
                throw new GradleException("Cannot delete \"jacoco.exec\" file. Check if some process is using it and close it.")
            }
        }

        task.onlyIf {
            File file = project.file("./jacoco.exec")
            return file.exists()
        }

        task.getOutputs().upToDateWhen {
            File file = project.file("./jacoco.exec")
            return !file.exists()
        }

        def cleanTask = project.tasks.findByName("clean")
        cleanTask.finalizedBy task
    }

}
