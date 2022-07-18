package com.mercadolibre.android.gradle.baseplugin.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan.BuildScanModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectVersion.ProjectVersionModule
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class ModuleConfigurerTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()

    private val moduleConfigurer = ModuleConfigurer()

    private val buildScanModule = mockk<BuildScanModule>(relaxed = true)
    private val listProjectsModule = mockk<ListProjectsModule>(relaxed = true)
    private val projectModule = mockk<ProjectVersionModule>(relaxed = true)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)

        mockkObject(ModuleProvider)

        every { ModuleProvider.provideProjectModules() } returns listOf(buildScanModule, listProjectsModule, projectModule)

        moduleConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the Build Scan Module`() {
        verify { buildScanModule.configure(root) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the ListProjectsModule Module`() {
        verify { listProjectsModule.configure(root) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the ProjectVersion Module`() {
        verify { projectModule.configure(root) }
    }
}
