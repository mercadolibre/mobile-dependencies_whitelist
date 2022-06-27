package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics.JacocoConfigurationExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain.BaseJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.DIR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXCLUDES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_ANDROID_EXCLUDE
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import java.io.File
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

abstract class AndroidJacocoModule: BaseJacocoModule() {

    fun findOrCreateJacocoTestReportTask(project: Project): TaskProvider<Task> {
        if (project.tasks.names.contains(JACOCO_TEST_REPORT_TASK)) {
            return project.tasks.named(JACOCO_TEST_REPORT_TASK)
        }

        val jacocoTestReportTask = project.tasks.register(JACOCO_TEST_REPORT_TASK)
        jacocoTestReportTask.configure {
            group = JACOCO_GROUP
        }
        return jacocoTestReportTask
    }

    fun createReportTask(variant: BaseVariant, project: Project): TaskProvider<JacocoReport> {
        val testTask = testTask(variant, project)

        val testTaskName = "jacoco${testTask.name.capitalize()}Report"

        val task = project.tasks.register(testTaskName, JacocoReport::class.java).apply {
            configure {
                configureReport(this, testTask, variant, project)
            }
        }
        return task
    }

    fun configureReport(report: JacocoReport, testTask: Test, variant: BaseVariant, project: Project) {
        val sourceDirs = sourceDirs(variant)
        val classesDir = classesDir(variant)

        with(report) {
            dependsOn(testTask)
            group = JACOCO_GROUP
            description = "$JACOCO_TEST_REPORT_DESCRIPTION for the ${variant.name} variant."
            executionData.from(project.files(executionDataFile(testTask)))

            val exclude = ArrayList<String>()

            findExtension<JacocoConfigurationExtension>(project)?.apply {
                exclude.addAll(this.excludeList)
            }

            exclude.addAll(JACOCO_ANDROID_EXCLUDE)

            val classDirectories = arrayListOf(project.fileTree(mutableMapOf(DIR_CONSTANT to classesDir, EXCLUDES_CONSTANT to exclude)))

            classDirectories.add(
                project.fileTree(
                    mutableMapOf(
                        DIR_CONSTANT to "${project.buildDir}/tmp/kotlin-classes/${variant.name}",
                        EXCLUDES_CONSTANT to exclude
                    )
                )
            )

            sourceDirs.add("src/main/kotlin")

            this.sourceDirectories.from(project.files(sourceDirs))
            this.classDirectories.from(classDirectories)
            this.executionData.from("${project.buildDir}/jacoco/test${variant.name}UnitTest.exec")

            reports {
                xml.required.set(true)
                csv.required.set(false)
                html.required.set(true)
            }
        }
    }

    private fun sourceDirs(variant: BaseVariant): ArrayList<String> {
        return arrayListOf<String>().apply {
            for (sourceSet in variant.sourceSets) {
                for (file in sourceSet.javaDirectories) {
                    add(file.path)
                }
            }
        }
    }

    private fun classesDir(variant: BaseVariant): File? {
        return VariantUtils.javaCompile(variant).destinationDirectory.asFile.orNull
    }

    private fun testTask(variant: BaseVariant, project: Project): Test {
        return project.tasks.named("test${variant.name.capitalize()}UnitTest", Test::class.java).get()
    }

    fun executionDataFile(testTask: Task): String {
        var path = ""
        findExtension<JacocoTaskExtension>(testTask)?.apply {
            path = destinationFile!!.path
        }
        return path
    }

}