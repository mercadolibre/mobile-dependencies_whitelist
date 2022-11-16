package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractPluginDescription
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.PLUGIN_MODULES_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AbstractModulePluginDescriptionTest : AbstractPluginManager() {

    private val pluginDescription = PluginDescriptionClassTest()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))

        every { mockedRoot.projectContent.tasks.register(ANY_NAME).get() } returns mockk(relaxed = true)
        every { mockedRoot.projectContent.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) } returns mockk(relaxed = true)
    }

    @org.junit.Test
    fun `When the any Plugin Description is called create the description Task, then the task exist in the project`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        // Return false when ask if the task exist to execute all the configuration
        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns false

        // Create and configure the project
        pluginDescription.configure(mockedSubProject)

        // The task was successfully created
        verify { mockedRootProject.tasks.register(ANY_NAME) }
        verify { mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) }
    }

    @org.junit.Test
    fun `When the any Plugin Description is called witouth the module task create the description Task`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        // Return false when ask if the task exist to execute all the configuration
        every { mockedRoot.projectContent.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) } returns null
        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns false

        // Create and configure the project
        pluginDescription.configure(mockedSubProject)

        // The module task was successfully created
        verify { mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) }
    }

    @org.junit.Test
    fun `When the any Plugin Description is called not create the description task because already exist`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        // Return true because already exist
        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns true

        // Check if is needed create another task
        pluginDescription.configure(mockedSubProject)

        // Another task is never created
        verify(inverse = true) { mockedRootProject.tasks.findByName(PLUGIN_MODULES_DESCRIPTION_TASK) }
    }

    @org.junit.Test
    fun `When the any Plugin Description is called multiple times, only one time create the task`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        // Create the task
        pluginDescription.configure(mockedSubProject)

        // When check if exist, return true
        every { mockedRootProject.tasks.names.contains(ANY_NAME) } returns true

        // Dont create the task
        pluginDescription.configure(mockedSubProject)

        // Once the task is created
        verify(exactly = 1) { mockedRootProject.tasks.register(any()) }
        verify(exactly = 1) { mockedRootProject.tasks.findByName(any()) }
    }

    @org.junit.Test
    fun `When the any Plugin Description task is called describe the functionality`() {
        mockkObject(OutputUtils)

        // Print the description
        pluginDescription.printMessage(pluginDescription.makeLog("Functionality", "Content"), "Title")

        // Log the description successfully
        verify { OutputUtils.logMessage("Title") }
        verify { OutputUtils.logMessage("- Functionality\nContent") }
    }

    class PluginDescriptionClassTest : AbstractPluginDescription(ANY_NAME, ANY_NAME, ANY_NAME, { ANY_NAME })
}
