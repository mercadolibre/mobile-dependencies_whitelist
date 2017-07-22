package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Created by saguilera on 7/22/17.
 */
class JavaJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        createJacocoReportTasks()
    }

    /**
     * Creates the tasks to generate Jacoco report, one per variant depending on Unit and Instrumentation tests.
     * [incubating]
     */
    private void createJacocoReportTasks() {
        project.sourceSets.all { variant ->
            createJacocoReportTask(variant?.name)
        }
        createJacocoReportTask()
    }

    private void createJacocoReportTask(String variant = null) {
        //Define local variables to avoid accessing multiple times to the buildType object.
        def buildTypeName = variant ?: ''
        def capitalizedBuildTypeName = buildTypeName.capitalize()

        def taskName = "jacoco${capitalizedBuildTypeName ?: 'Release'}"
        def testTaskName = "test${capitalizedBuildTypeName}"

        //Create and retrieve necesary tasks
        def jacocoTask = project.tasks.create taskName, JacocoReport
        def unitTest = project.tasks.findByName(testTaskName)

        //Define JacocoTasks and it's configuration
        jacocoTask.description = "Generate Jacoco code coverage report after running tests for ${capitalizedBuildTypeName} flavor."
        jacocoTask.group = "Reporting"

        //By convention this is the sources folder
        jacocoTask.sourceDirectories = project.files("src/main/java")

        //Here is where execution data files are created. ConnectedAndroidTest and Test tasks generates them.
        jacocoTask.executionData = project.files("build/jacoco/${testTaskName}.exec")

        def jacocoDirectory = "./build/intermediates/classes/"
        jacocoDirectory += "${buildTypeName}"

        //Ignore auto-generated classes
        jacocoTask.classDirectories = project.fileTree(dir: jacocoDirectory, excludes: [
                '**/BuildConfig.class',
                '**/*$ViewInjector*.*',
                '**/*$ViewBinder*.*',
                '**/*$Lambda$*.*',
                '**/*Module.*',
                '**/*Dagger*.*',
                '**/*MembersInjector*.*',
                '**/*_Provide*Factory*.*',
                '**/*_Factory*.*',
                '**/*$*$*.*'
        ])

        //Enable both reports
        jacocoTask.reports.xml.enabled = true
        jacocoTask.reports.html.enabled = true

        if (unitTest) {
            jacocoTask.dependsOn unitTest
        }
    }

}
