package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.app.core.action.modules.plugin_description.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import io.mockk.mockk
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AbstractModulePluginDescriptionTest {

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is created workd`() {
        val pluginDescription = AppPluginDescriptionModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.configureTask(task)
        assert(pluginDescription.makeMessage(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")
    }
}