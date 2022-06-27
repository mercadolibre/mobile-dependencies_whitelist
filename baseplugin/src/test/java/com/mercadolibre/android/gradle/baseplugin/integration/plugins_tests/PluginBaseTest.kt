package com.mercadolibre.android.gradle.baseplugin.integration.plugins_tests

import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_TASK
import com.mercadolibre.android.gradle.baseplugin.integration.utils.TaskTest
import org.gradle.api.Project

object PluginBaseTest: TaskTest() {

    fun pluginDescriptionTask(project: Project) {
        assert(project.tasks.findByName(PLUGIN_DESCRIPTION_TASK) != null)
        assert(project.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) != null)
    }

    fun listProjectsTask(project: Project) {
        assert(project.tasks.findByName(TASK_GET_PROJECT_TASK) != null)
    }

    fun getProjectVersionTask(project: Project) {
        assert(project.tasks.findByName(TASK_GET_PROJECT_TASK) != null)
    }

    fun jacocoTasks(project: Project) {
        assert(findExtension(JACOCO_EXTENSION, project))
    }
}