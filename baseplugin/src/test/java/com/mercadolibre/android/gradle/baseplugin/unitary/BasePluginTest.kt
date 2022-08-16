package com.mercadolibre.android.gradle.baseplugin.unitary

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.BaseSettingsPlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.gradle.api.initialization.Settings
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BasePluginTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()
    val baseSettingsPlugin = BaseSettingsPlugin()

    val basicsConfigurer: BasicsConfigurer = mock {}
    val extensionsConfigurer: ExtensionsConfigurer = mock {}
    val moduleConfigurer: ModuleConfigurer = mock {}

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        basePlugin.configurers.clear()
        basePlugin.configurers.addAll(listOf(basicsConfigurer, extensionsConfigurer, moduleConfigurer))

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)

        basePlugin.apply(root)
    }

    @org.junit.Test
    fun `When the BasePlugin is applied the BasicsConfigurer configures the project`() {
        verify(basicsConfigurer).configureProject(root)
    }

    @org.junit.Test
    fun `When the BasePlugin is applied the ExtensionsConfigurer configures the project`() {
        verify(extensionsConfigurer).configureProject(root)
    }

    @org.junit.Test
    fun `When the BasePlugin is applied the ModuleConfigurer configures the project`() {
        verify(moduleConfigurer).configureProject(root)
    }

    @org.junit.Test
    fun `When the BasePlugin is applied the ModuleConfigurer configures the settings`() {
        val settings = mockk<Settings>(relaxed = true)
        val module = mockk<SettingsModule>(relaxed = true)

        mockkObject(ModuleProvider)

        every { ModuleProvider.provideSettingsModules() } returns listOf(module)

        baseSettingsPlugin.apply(settings)

        io.mockk.verify { module.configure(settings) }
    }
}
