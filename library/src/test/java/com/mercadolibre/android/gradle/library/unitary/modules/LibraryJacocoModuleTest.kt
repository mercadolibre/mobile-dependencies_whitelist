package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.utils.domain.ModuleType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryJacocoModuleTest : AbstractPluginManager() {

    val jacocoModule = LibraryJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)

        projects[LIBRARY_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest", Test::class.java)

        jacocoModule.configure(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule is called before evaluate execute her configuration`() {
        val project = mockk<Project>(relaxed = true)

        every { project.tasks.register(JACOCO_FULL_REPORT_TASK).get() } returns mockk(relaxed = true)

        jacocoModule.moduleConfiguration(project)

        verify { project.tasks.named(JACOCO_FULL_REPORT_TASK) }
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule configures all the Variants tasks`() {
        val taskProvider = mockk<TaskProvider<Task>>(relaxed = true)

        jacocoModule.configVariantsTasks(projects[LIBRARY_PROJECT]!!, taskProvider)

        verify { taskProvider.configure(any()) }
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule configures the project create JacocoFullReport Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(JACOCO_FULL_REPORT_TASK) != null)
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule configures the project create JacocoTestReport Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(JACOCO_TEST_REPORT_TASK) != null)
    }
}
