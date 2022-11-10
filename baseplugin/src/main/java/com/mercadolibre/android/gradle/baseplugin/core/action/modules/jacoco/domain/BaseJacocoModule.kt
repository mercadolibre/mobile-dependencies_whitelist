package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain

import com.android.build.gradle.internal.crash.afterEvaluate
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics.JacocoConfigurationExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_VERIFICATION_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.TEST_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

/**
 * Base Jacoco Module is in charge of adding the Jacoco Plugin and generating the basic tasks for its operation.
 */
open class BaseJacocoModule : Module() {

    /**
     * This method is responsible for applying the Jacoco plugin and configuring its main tasks.
     */
    override fun configure(project: Project) {
        project.plugins.apply(JacocoPlugin::class.java)

        val task = project.tasks.register(JACOCO_FULL_REPORT_TASK).get()
        task.group = JACOCO_GROUP

        configureTasks(project)
        afterEvaluate {
            createNeededTasks(project)
            configureTasks(project)
        }
    }

    /**
     * This method is in charge to config the Report Tasks.
     */
    fun configureTasks(project: Project) {
        project.tasks.withType<Test> {
            configure<JacocoTaskExtension> {
                this.excludes = mutableListOf("jdk.internal.*")
                isIncludeNoLocationClasses = true
            }
            testLogging {
                events = setOf(TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }

    /**
     * If they do not exist, this method is responsible for generating the basic Jacoco tasks.
     */
    fun createNeededTasks(project: Project) {
        if (project.tasks.findByName(JACOCO_TEST_REPORT_TASK) == null) {
            val task = project.tasks.register(JACOCO_TEST_REPORT_TASK).get()
            task.group = JACOCO_VERIFICATION_GROUP
        }

        if (project.tasks.findByName(TEST_TASK) == null) {
            val task = project.tasks.register(TEST_TASK).get()
            task.group = JACOCO_VERIFICATION_GROUP
        }
    }

    /**
     * This method is responsible for providing the extension name that Jacoco needs.
     */
    override fun getExtensionName(): String = JACOCO_EXTENSION

    /**
     * This method is responsible for providing the extension that Jacoco needs to work properly.
     */
    override fun createExtension(project: Project) {
        project.extensions.create(JACOCO_EXTENSION, JacocoConfigurationExtension::class.java)
    }
}
