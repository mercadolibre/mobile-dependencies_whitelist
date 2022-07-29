package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractModulePluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_DESCRIPTION_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.nhaarman.mockitokotlin2.times
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AbstractModulePluginDescriptionTest : AbstractPluginManager() {

    val pluginDescription = PluginDescriptionClassTest()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))

        every { mockedRoot.projectContent.tasks.register(ANY_NAME).get() } returns mockk(relaxed = true)
        every { mockedRoot.projectContent.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) } returns mockk(relaxed = true)
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is create the description Task`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns false

        pluginDescription.configure(mockedSubProject)

        verify { mockedRootProject.tasks.register(ANY_NAME) }
        verify { mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) }
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is create the description Task and Module task not exist`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        every { mockedRoot.projectContent.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) } returns null
        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns false

        pluginDescription.configure(mockedSubProject)

        verify { mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) }
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest not create the description if exist`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns true

        pluginDescription.configure(mockedSubProject)

        verify(inverse = true) {
            mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK)
        }
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is not create the description Task if exist`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        pluginDescription.configure(mockedSubProject)
        pluginDescription.configure(mockedSubProject)

        verify {
            mockedRootProject.tasks.register(ANY_NAME)
            times(1)
        }
        verify {
            mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK)
            times(1)
        }
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest task is called describe the functionality`() {
        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.makeMessage(ANY_NAME, ANY_NAME)

        pluginDescription.configureTask(task)

        verify { task.group = MELI_GROUP }
        verify { task.description = PLUGIN_DESCRIPTION_DESCRIPTION }
    }

    class PluginDescriptionClassTest : AbstractModulePluginDescription(ANY_NAME, ANY_NAME, { ANY_NAME })
}
