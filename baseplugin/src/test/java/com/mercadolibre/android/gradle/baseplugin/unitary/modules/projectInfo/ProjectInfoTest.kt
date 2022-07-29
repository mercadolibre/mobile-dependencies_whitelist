package com.mercadolibre.android.gradle.baseplugin.unitary.modules.projectInfo

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectInfo.ProjectInfoModule
import com.mercadolibre.android.gradle.baseplugin.core.components.PROJECT_INFO_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectInfoTest : AbstractPluginManager() {

    private val projectInfo = ProjectInfoModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
        every { mockedRoot.subProjects[LIBRARY_PROJECT]!!.project.tasks.register(PROJECT_INFO_TASK).get() } returns mockk(relaxed = true)
    }

    @org.junit.Test
    fun `When the ProjectInfoModule is called configure the project`() {
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        projectInfo.configure(mockedSubProject)

        io.mockk.verify { mockedSubProject.tasks }
    }

    @org.junit.Test
    fun `When the ProjectInfoModule is called get info of a module`() {
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!
        val mockedBaseExtension = getMockedExtension<BaseExtension>(mockedSubProject)

        projectInfo.getInfo(mockedSubProject.project)

        io.mockk.verify { mockedBaseExtension.compileSdkVersion }
        io.mockk.verify { mockedBaseExtension.defaultConfig }
        io.mockk.verify { mockedBaseExtension.compileOptions }
    }

    @org.junit.Test
    fun `When the ProjectInfoModule is called get info of all modules`() {
        val mockedRootProject = mockedRoot.projectContent.project
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        projectInfo.printConfiguration(mockedRootProject)

        io.mockk.verify { mockedRootProject.subprojects }
        io.mockk.verify { mockedSubProject.name }
    }
}
