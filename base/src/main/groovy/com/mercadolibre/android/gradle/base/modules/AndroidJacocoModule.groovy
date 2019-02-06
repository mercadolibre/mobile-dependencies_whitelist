package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Module in charge of configuring jacoco reports for android projects so that any coverage application can obtain them
 *
 * Created by saguilera on 7/22/17.
 */
class AndroidJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        project.android {
            testOptions {
                unitTests.all {
                    jacoco {
                        includeNoLocationClasses = true
                    }
                }
            }
        }

        Task jacocoTestReportTask = findOrCreateJacocoTestReportTask()

        getVariants().all { variant ->
            JacocoReport reportTask = createReportTask(variant)
            jacocoTestReportTask.dependsOn reportTask
        }

        if (project.tasks.findByName(JACOCO_FULL_REPORT_TASK)) {
            project.tasks."$JACOCO_FULL_REPORT_TASK".dependsOn jacocoTestReportTask
        } else {
            project.tasks.whenTaskAdded {
                if (it.name.contentEquals(JACOCO_FULL_REPORT_TASK)) {
                    it.dependsOn jacocoTestReportTask
                }
            }
        }
    }

    private def findOrCreateJacocoTestReportTask() {
        Task jacocoTestReportTask = project.tasks.findByName("jacocoTestReport")
        if (!jacocoTestReportTask) {
            jacocoTestReportTask = project.task("jacocoTestReport") {
                group = "reporting"
            }
        }
        return jacocoTestReportTask
    }

    private def getVariants() {
        if (project.android.hasProperty('libraryVariants')) {
            return project.android.libraryVariants
        } else {
            return project.android.applicationVariants
        }
    }

    private def createReportTask(def variant) {
        def sourceDirs = sourceDirs(variant)
        def classesDir = classesDir(variant)
        def testTask = testTask(variant)
        def executionData = executionDataFile(testTask)
        return project.task("jacoco${testTask.name.capitalize()}Report", type: JacocoReport) { JacocoReport reportTask ->
            reportTask.dependsOn testTask
            reportTask.group = "reporting"
            reportTask.description = "Generates Jacoco coverage reports for the ${variant.name} variant."
            reportTask.executionData = project.files(executionData)
            def exclude = [
                    '**/R.class',
                    '**/R$*.class',
                    '**/BuildConfig.class',
                    '**/*$ViewInjector*.*',
                    '**/*$ViewBinder*.*',
                    '**/Manifest*.*',
                    '**/*$Lambda$*.*',
                    '**/*Module.*',
                    '**/*Dagger*.*',
                    '**/*MembersInjector*.*',
                    '**/*_Provide*Factory*.*',
                    '**/*_Factory*.*',
                    '**/*$*$*.*'
            ]

            def sourceDirectories = sourceDirs
            def classDirectories = project.fileTree(dir: classesDir, excludes: exclude)

            if (project.plugins.hasPlugin("kotlin-android")) {
                classDirectories += project.fileTree(
                        dir: "${project.buildDir}/tmp/kotlin-classes/releaseUnitTest",
                        excludes: exclude
                )
                sourceDirectories.add("src/main/kotlin")
            }

            reportTask.sourceDirectories = project.files(sourceDirectories)
            reportTask.classDirectories = classDirectories

            reportTask.reports {
                csv.enabled false
                html.enabled true
                xml.enabled true
            }
        }
    }

    protected def sourceDirs(variant) {
        variant.sourceSets.java.srcDirs.collect { it.path }.flatten()
    }

    protected def classesDir(variant) {
        variant.javaCompile.destinationDir
    }

    protected def testTask(variant) {
        project.tasks.withType(Test).find { task -> task.name =~ /test${variant.name.capitalize()}UnitTest/ }
    }

    protected def executionDataFile(Task testTask) {
        testTask.jacoco.destinationFile.path
    }

}
