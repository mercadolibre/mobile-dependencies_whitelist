package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain

import com.android.build.gradle.internal.crash.afterEvaluate
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics.JacocoConfigurationExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_VERIFICATION_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.TEST_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.ExtensionProvider
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.apply

/**
 * Base Jacoco Module is in charge of adding the Jacoco Plugin and generating the basic tasks for its operation.
 */
open class BaseJacocoModule : Module, ExtensionProvider, ExtensionGetter() {

    /**
     * This method is responsible for providing the name of the Jacoco extension.
     */
    override fun getName(): String = JACOCO_EXTENSION

    /**
     * This method is responsible for applying the Jacoco plugin and configuring its main tasks.
     */
    override fun configure(project: Project) {
        project.apply(plugin = JACOCO_PLUGIN)

        project.tasks.withType(Test::class.java).configureEach {
            testLogging {
                events = setOf(TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.FULL
            }
        }

        project.tasks.register(JACOCO_FULL_REPORT_TASK).configure {
            this.group = JACOCO_GROUP
        }

        afterEvaluate {
            createNeededTasks(project)
        }
    }

    /**
     * If they do not exist, this method is responsible for generating the basic Jacoco tasks.
     */
    fun createNeededTasks(project: Project) {
        if (project.tasks.findByName(JACOCO_TEST_REPORT_TASK) == null) {
            project.tasks.register(JACOCO_TEST_REPORT_TASK).configure {
                group = JACOCO_VERIFICATION_GROUP
            }
        }

        if (project.tasks.findByName(TEST_TASK) == null) {
            project.tasks.register(TEST_TASK).configure {
                group = JACOCO_VERIFICATION_GROUP
            }
        }
    }

    /**
     * This method is responsible for providing the extension that Jacoco needs to work properly.
     */
    override fun createExtension(project: Project) {
        project.extensions.create(getName(), JacocoConfigurationExtension::class.java)
        for (subProjects in project.subprojects) {
            subProjects.extensions.create(getName(), JacocoConfigurationExtension::class.java)
        }
    }
}
