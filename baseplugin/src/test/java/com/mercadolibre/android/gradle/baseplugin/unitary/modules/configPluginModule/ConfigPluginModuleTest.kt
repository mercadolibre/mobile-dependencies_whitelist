package com.mercadolibre.android.gradle.baseplugin.unitary.modules.configPluginModule

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.configPlugin.ConfigPluginModule
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.OutPutUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.DF_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConfigPluginModuleTest : AbstractPluginManager() {

    val configModule = ConfigPluginModule()

    val MELI_LIBRARY_PLUGIN = "mercadolibre.gradle.config.library"
    val MELI_APP_PLUGIN = "mercadolibre.gradle.config.app"
    val MELI_DF_PLUGIN = "mercadolibre.gradle.config.dynamicfeatures"

    val LIBRARY_PLUGIN = "com.android.library"
    val APP_PLUGIN = "com.android.application"
    val DF_PLUGIN = "com.android.dynamic-feature"

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        mockkObject(OutPutUtils)

        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT, APP_PROJECT, DF_PROJECT))
    }

    @org.junit.After
    fun after() {
        unmockkAll()
    }

    @org.junit.Test
    fun `When the ConfigModuleTest is called check all subprojects`() {
        val rootProject = mockk<Project>()
        val subProject = mockk<Project>(relaxed = true)

        every { rootProject.subprojects } returns mutableSetOf(subProject)

        configModule.configure(rootProject)

        verify { rootProject.subprojects }
        verify { subProject.hashCode() }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest is called create her task`() {
        val subProject = mockk<Project>(relaxed = true)
        val task = mockk<Task>(relaxed = true)

        every { subProject.tasks.register("lintMeliPlugins").get() } returns task

        configModule.createTask(subProject)

        verify { task.group = MELI_GROUP }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest and any Library module without meli plugin exists then send the module name in a warning`() {
        val mockedProjectSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        every { mockedProjectSubProject.plugins.findPlugin(LIBRARY_PLUGIN) } returns mockk(relaxed = true)
        every { mockedProjectSubProject.plugins.findPlugin(APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(DF_PLUGIN) } returns null

        configModule.lint(mockedProjectSubProject)

        verify { OutPutUtils.sendAWarningMessage("p1 needs Meli Library Plugin") }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest and any App module without meli plugin exists then send the module name in a warning`() {
        val mockedProjectSubProject = mockedRoot.subProjects[APP_PROJECT]!!.project

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        every { mockedProjectSubProject.plugins.findPlugin(LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(APP_PLUGIN) } returns mockk(relaxed = true)
        every { mockedProjectSubProject.plugins.findPlugin(DF_PLUGIN) } returns null

        configModule.lint(mockedProjectSubProject)

        verify { OutPutUtils.sendAWarningMessage("p2 needs Meli App Plugin") }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest and any DF module without meli plugin exists then send the module name in a warning`() {
        val mockedProjectSubProject = mockedRoot.subProjects[DF_PROJECT]!!.project

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        every { mockedProjectSubProject.plugins.findPlugin(LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(DF_PLUGIN) } returns mockk(relaxed = true)

        configModule.lint(mockedProjectSubProject)

        verify { OutPutUtils.sendAWarningMessage("p4 needs Meli Dynamicfeatures Plugin") }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest and any unknown module type exists then not send the module name in a warning`() {
        val mockedProjectSubProject = mockedRoot.subProjects[DF_PROJECT]!!.project

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        every { mockedProjectSubProject.plugins.findPlugin(LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(DF_PLUGIN) } returns null

        configModule.lint(mockedProjectSubProject)

        verify(exactly = 0) { OutPutUtils.sendAWarningMessage(any()) }
    }

    @org.junit.Test
    fun `When the ConfigModuleTest and all module are configurated then do nothing`() {
        val mockedProjectSubProject = mockedRoot.subProjects[DF_PROJECT]!!.project

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns mockk(relaxed = true)
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        configModule.lint(mockedProjectSubProject)

        verify(exactly = 0) { OutPutUtils.sendAWarningMessage(any()) }
    }

    @org.junit.Test(expected = GradleException::class)
    fun `When the ConfigModuleTest and any Android module without meli plugin exists and isBlocker then send a gradle error with a message`() {
        val mockedProjectSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        configModule.isBlocker = true

        every { mockedProjectSubProject.plugins.findPlugin(MELI_LIBRARY_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(MELI_DF_PLUGIN) } returns null

        every { mockedProjectSubProject.plugins.findPlugin(LIBRARY_PLUGIN) } returns mockk(relaxed = true)
        every { mockedProjectSubProject.plugins.findPlugin(APP_PLUGIN) } returns null
        every { mockedProjectSubProject.plugins.findPlugin(DF_PLUGIN) } returns null

        configModule.lint(mockedProjectSubProject)

        verify { OutPutUtils.sendAWarningMessage("p1 needs Meli Library Plugin") }
    }
}
