package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listVariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants.ListVariantsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LIST_VARIANTS_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListVariantsTest : AbstractPluginManager() {

    private val listVariants = ListVariantsModule()

    @org.junit.Before
    fun setUp() {

        mockkObject(OutputUtils)

        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT, APP_PROJECT))

        val libraryProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!
        val appProject = mockedRoot.subProjects[APP_PROJECT]!!

        val libraryExtension = mockk<LibraryExtension>(relaxed = true) {
            every { buildTypes.names } returns sortedSetOf("variantLib1", "variantLib2")
        }

        val appExtension = mockk<AppExtension>(relaxed = true) {
            every { buildTypes.names } returns sortedSetOf("variantApp1", "variantApp2")
        }

        every { libraryProject.extension.findByType(AppExtension::class.java) } returns null
        every { libraryProject.extension.findByType(LibraryExtension::class.java) } returns libraryExtension
        every { appProject.extension.findByType(LibraryExtension::class.java) } returns null
        every { appProject.extension.findByType(AppExtension::class.java) } returns appExtension
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
    fun `When the ListVariantsModule is called detect all project types and report it`() {
        val mockedRootProject = mockedRoot.projectContent

        listVariants.printVariants(mockedRootProject.project)

        // Check the root project name
        verify { OutputUtils.logMessage("Root Project: root".ansi(ANSI_GREEN)) }

        // Get the module Library
        verify { OutputUtils.logMessage("p1 - ModuleType: Library\n") }

        // Get Library variants
        verify { OutputUtils.logMessage("variantLib1".ansi(ANSI_YELLOW)) }
        verify { OutputUtils.logMessage("variantLib2".ansi(ANSI_YELLOW)) }

        // Get the module App
        verify { OutputUtils.logMessage("p2 - ModuleType: App\n") }

        // Get App variants
        verify { OutputUtils.logMessage("variantApp1".ansi(ANSI_YELLOW)) }
        verify { OutputUtils.logMessage("variantApp2".ansi(ANSI_YELLOW)) }
    }
}
