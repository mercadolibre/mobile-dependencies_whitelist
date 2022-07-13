package com.mercadolibre.android.gradle.baseplugin.unitary.modules.buildscan

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.scan.plugin.BuildScanExtension
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan.BuildScanModule
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class BuildScanTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()
    val buildScan = BuildScanModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        basePlugin.apply(root)
        runGradle(tmpFolder.root)
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure build scan extension the project`() {
        val extension = mockk<BuildScanExtension>(relaxed = true)
        buildScan.configBuildScanExtension(extension, ANY_NAME)
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure her background tasks`() {
        val extension = mockk<BuildScanExtension>(relaxed = true)
        buildScan.configBackground(extension)
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure the project`() {
        val project = mockk<Project>(relaxed = true)

        every { project.extensions.findByType(GradleEnterpriseExtension::class.java) } returns mockk(relaxed = true)

        buildScan.configure(project)
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure the setting`() {
        val settings = mockk<Settings>(relaxed = true)

        every { settings.extensions.findByType(GradleEnterpriseExtension::class.java) } returns mockk(relaxed = true)

        buildScan.configure(settings)
    }
}
