package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.PluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_SUB_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Task

class PluginDescriptionModuleTest : AbstractPluginManager() {

    private val pluginDescription = PluginDescriptionModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
    }

    @org.junit.Test
    fun `When the any PluginDescriptionModuleTest is called create the description Task`() {
        val mockedProjectRoot = mockedRoot.projectContent.project
        val taskDescription = mockk<Task>(relaxed = true)
        val taskModule = mockk<Task>(relaxed = true)

        every { mockedRoot.projectContent.tasks.register(PLUGIN_MODULES_DESCRIPTION_TASK).get() } returns taskDescription
        every { mockedRoot.projectContent.tasks.register(PLUGIN_DESCRIPTION_TASK).get() } returns taskModule
        every { mockedRoot.projectContent.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) } returns taskDescription

        pluginDescription.configure(mockedProjectRoot)

        verify { taskDescription.group = MELI_SUB_GROUP }
        verify { taskDescription.description = PLUGIN_DESCRIPTION_DESCRIPTION }

        verify { taskModule.group = MELI_GROUP }
        verify { taskModule.description = PLUGIN_DESCRIPTION_DESCRIPTION }
    }
}
