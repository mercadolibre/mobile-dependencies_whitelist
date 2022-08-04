package com.mercadolibre.android.gradle.baseplugin.unitary.modules

import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ModuleTest : AbstractPluginManager() {

    private val project = mockRootProject(listOf())

    @org.junit.Test
    fun `When any Module is instatiate, create her extension`() {
        val action = mockk<Task>()
        val module = ModuleExample(action)
        val mockeProjectRoot = project.projectContent.project
        val mockeRoot = mockk<Project>(relaxed = true)

        module.moduleConfiguration(mockeRoot)

        every { project.projectContent.extension.create(ANY_NAME, ModuleOnOffExtension::class.java) } returns mockk(relaxed = true)

        module.createExtension(mockeProjectRoot)

        verify { project.projectContent.extension.create(ANY_NAME, ModuleOnOffExtension::class.java) }
    }

    @org.junit.Test
    fun `When any Module is instatiate, check if is enabled`() {
        val action = mockk<Task>(relaxed = true)
        val module = ModuleExample(action)
        val mockedProjectRoot = project.projectContent.project
        val extensionOnOff = mockk<ModuleOnOffExtension>(relaxed = true) {
            every { enabled } returns true
        }

        every { findExtension(mockedProjectRoot, ANY_NAME) as? ModuleOnOffExtension } returns extensionOnOff

        module.executeModule(mockedProjectRoot)

        verify { extensionOnOff.enabled }
        verify { action.group = ANY_NAME }
    }

    @org.junit.Test
    fun `When any Module is instatiate, check if is disabled`() {
        val action = mockk<Task>(relaxed = true)
        val module = ModuleExample(action)
        val mockedProjectRoot = project.projectContent.project
        val extensionOnOff = mockk<ModuleOnOffExtension>(relaxed = true) {
            every { enabled } returns false
        }

        every { findExtension(mockedProjectRoot, ANY_NAME) as? ModuleOnOffExtension } returns extensionOnOff

        module.executeModule(mockedProjectRoot)

        verify { extensionOnOff.enabled }
    }

    @org.junit.Test
    fun `When any Module is instatiate, check if is extension not exist`() {
        val action = mockk<Task>(relaxed = true)
        val module = ModuleExample(action)
        val mockedProjectRoot = project.projectContent.project

        every { findExtension(mockedProjectRoot, ANY_NAME) as? ModuleOnOffExtension } returns null

        module.executeModule(mockedProjectRoot)

        verify { action.group = ANY_NAME }
    }

    class ModuleExample(private val action: Task) : Module() {

        override fun createExtension(project: Project) {
            project.extensions.create(ANY_NAME, ModuleOnOffExtension::class.java)
        }

        override fun getExtensionName(): String = ANY_NAME

        override fun configure(project: Project) {
            action.group = ANY_NAME
        }
    }
}
