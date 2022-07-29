package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listVariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants.ListVariantsModule
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListVariantsTest : AbstractPluginManager() {

    private val listVariants = ListVariantsModule()

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT, APP_PROJECT))
    }

    @org.junit.Test
    fun `When the ProjectVersion is called configure the task`() {
        val mockedProjectRoot = mockedRoot.projectContent.project
        val task = mockk<Task>(relaxed = true)

        every { mockedProjectRoot.tasks.register(LIST_VARIANTS_TASK).get() } returns task

        listVariants.configure(mockedProjectRoot)

        verify { task.group = MELI_GROUP }
        verify { task.description = LIST_VARIANTS_DESCRIPTION }
    }

    @org.junit.Test
    fun `When the ListVariantsModule is called configure the project`() {
        val mockedRootProject = mockedRoot.projectContent
        val mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!

        val libraryExtension = getMockedExtension<LibraryExtension>(mockedSubProject)
        val appExtension = getMockedExtension<AppExtension>(mockedSubProject)

        listVariants.printVariants(mockedRootProject.project)

        verify { mockedRootProject.project.name }
        verify { mockedSubProject.project.name }
        verify { libraryExtension.buildTypes }
        verify { appExtension.buildTypes }
    }
}
