package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listProjects

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_PROJECTS_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListProjectsTest : AbstractPluginManager() {

    private val listProjects = ListProjectsModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
    }

    @org.junit.Test
    fun `When the ListProjectsModule is called configure the project`() {
        val mockedRootProject = mockedRoot.projectContent.project
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        every { mockedRoot.projectContent.tasks.register(LIST_PROJECTS_TASK).get() } returns mockk(relaxed = true)

        listProjects.configure(mockedRootProject)
        listProjects.printProjects(mockedRootProject)

        verify { mockedRootProject.tasks }
        verify { mockedRootProject.subprojects }
        verify { mockedSubProject.name }
    }
}
