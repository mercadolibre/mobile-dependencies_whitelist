package com.mercadolibre.android.gradle.baseplugin.unitary.modules.projectVersion

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectVersion.ProjectVersionModule
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.FILE_NAME_PROJECT_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.TASK_GET_PROJECT_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.FileOutputStream

@RunWith(JUnit4::class)
class ProjectVersionTest : AbstractPluginManager() {

    private val projectVersion = ProjectVersionModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
    }

    @org.junit.Test
    fun `When the ProjectVersion is called configure the task`() {
        val mockedProjectRoot = mockedRoot.projectContent.project
        val task = mockk<Task>(relaxed = true)

        every { mockedProjectRoot.tasks.register(TASK_GET_PROJECT_TASK).get() } returns task

        projectVersion.configure(mockedProjectRoot)

        verify { task.group = MELI_GROUP }
        verify { task.description = TASK_GET_PROJECT_DESCRIPTION }
    }

    @org.junit.Test
    fun `When the ProjectVersion is called configure the project`() {
        val mockedProjectRoot = mockedRoot.projectContent.project

        mockkConstructor(FileOutputStream::class)

        val build = mockk<File> {
            every { exists() } returns false
            every { mkdirs() } returns true
        }
        val input = File("$BUILD_CONSTANT/$FILE_NAME_PROJECT_VERSION")

        projectVersion.printProjectVersion(build, input, mockedProjectRoot)

        verify { mockedProjectRoot.version }
    }
}
