package com.mercadolibre.android.gradle.library.integration.plugins_tests

import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.library.integration.utils.TaskTest
import org.gradle.api.Project

object PluginBaseTest: TaskTest() {

    fun jacocoTasks(project: Project) {
        assert(project.tasks.findByName(JACOCO_FULL_REPORT_TASK) != null)
        assert(project.tasks.findByName(JACOCO_TEST_REPORT_TASK) != null)
    }

    fun lintTasks(project: Project) {
        assert(findExtension(LINTABLE_EXTENSION, project))
        assert(project.tasks.findByName("lint") != null)
    }

}