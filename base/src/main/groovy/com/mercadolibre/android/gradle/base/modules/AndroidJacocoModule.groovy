package com.mercadolibre.android.gradle.base.modules

import com.mercadolibre.android.gradle.base.utils.VariantUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.platform.base.Variant
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

        TaskProvider<Task> jacocoTestReportTask = findOrCreateJacocoTestReportTask()

        getVariants().all { variant ->
            TaskProvider<JacocoReport> reportTask = createReportTask(variant)
            jacocoTestReportTask.configure {
                dependsOn reportTask
            }
        }

        if (project.tasks.names.contains(JACOCO_FULL_REPORT_TASK)) {
            project.tasks.named(JACOCO_FULL_REPORT_TASK).configure {
                dependsOn jacocoTestReportTask
            }
        } else {
            project.tasks.configureEach {
                if (it.name.contentEquals(JACOCO_FULL_REPORT_TASK)) {
                    it.dependsOn jacocoTestReportTask
                }
            }
        }
    }

    private TaskProvider<Task> findOrCreateJacocoTestReportTask() {
        final String taskName = "jacocoTestReport"
        if (project.tasks.names.contains(taskName)) {
            return project.tasks.named(taskName)
        }

        TaskProvider<Task> jacocoTestReportTask = project.tasks.register(taskName)
        jacocoTestReportTask.configure {
            group = "reporting"
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

    private TaskProvider<JacocoReport> createReportTask(def variant) {
        def sourceDirs = sourceDirs(variant)
        def classesDir = classesDir(variant)
        TaskProvider<Test> testTask = testTask(variant)

        TaskProvider<JacocoReport> reportTaskProvider = project.tasks.register("jacoco${testTask.name.capitalize()}Report", JacocoReport)
        reportTaskProvider.configure { JacocoReport reportTask ->
            reportTask.dependsOn testTask
            reportTask.group = "reporting"
            reportTask.description = "Generates Jacoco coverage reports for the ${variant.name} variant."
            reportTask.executionData.from = project.files(executionDataFile(testTask.get()))
            def exclude = project.jacocoConfiguration.excludeList
            def defaultExclude = [
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
            exclude.addAll(defaultExclude)

            def sourceDirectories = sourceDirs
            def classDirectories = project.fileTree(dir: classesDir, excludes: exclude)

            if (project.plugins.hasPlugin("kotlin-android")) {
                classDirectories += project.fileTree(
                        dir: "${project.buildDir}/tmp/kotlin-classes/${variant.name}",
                        excludes: exclude
                )
                sourceDirectories.add("src/main/kotlin")
            }

            reportTask.sourceDirectories.from = project.files(sourceDirectories)
            reportTask.classDirectories.from = classDirectories

            reportTask.reports {
                csv.enabled false
                html.enabled true
                xml.enabled true
            }
        }

        return reportTaskProvider
    }

    protected def sourceDirs(variant) {
        variant.sourceSets.java.srcDirs.collect { it.path }.flatten()
    }

    // Kotlin generated code output folder comes from:
    // https://github.com/JetBrains/kotlin/blob/v1.3.11/libraries/tools/kotlin-gradle-plugin/src/main/kotlin/org/jetbrains/kotlin/gradle/plugin/KotlinPlugin.kt#L726
    protected def classesDir(variant) {
        VariantUtils.javaCompile(variant).destinationDir
    }

    protected TaskProvider<Test> testTask(variant) {
        project.tasks.named("test${variant.name.capitalize()}UnitTest", Test)
    }

    protected def executionDataFile(Task testTask) {
        testTask.jacoco.destinationFile.path
    }
}
