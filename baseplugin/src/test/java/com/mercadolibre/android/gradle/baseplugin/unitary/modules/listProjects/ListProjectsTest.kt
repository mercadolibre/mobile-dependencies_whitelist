package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listProjects

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_PROJECTS_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.DF_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListProjectsTest : AbstractPluginManager() {

    private val listProjects = ListProjectsModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT, APP_PROJECT, DF_PROJECT))
    }

    @org.junit.Test
    fun `When the ListProjectsModule is called created her task correctly`() {
        val mockedRootProject = mockedRoot.projectContent.project

        // Mock the creationg of the task
        every { mockedRootProject.tasks.register(LIST_PROJECTS_TASK).get() } returns mockk(relaxed = true)
        every { mockedRootProject.tasks.findByName(LIST_PROJECTS_TASK) } returns mockk(relaxed = true)

        // Configure and create the task
        listProjects.configure(mockedRootProject)

        // Check if the task exist
        assert(mockedRootProject.tasks.findByName(LIST_PROJECTS_TASK) != null)
    }

    @org.junit.Test
    fun `When the ListProjectsModule is called configure the project`() {
        val mockedRootProject = mockedRoot.projectContent.project

        mockkObject(OutputUtils)

        // Print all of the projects
        listProjects.printProjects(mockedRootProject)

        // Print the title
        verify { OutputUtils.logMessage("=== BEGINNING OF PROJECTS LIST ===") }

        // Print the name of the projects
        verify { OutputUtils.logMessage("p1") }
        verify { OutputUtils.logMessage("p2") }
        verify { OutputUtils.logMessage("p4") }
    }
}
