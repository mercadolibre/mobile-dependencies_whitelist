package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class ModuleConfigurerTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()

    val moduleConfigurer = AppModuleConfigurer()

    val lintableModule = mockk<LintableModule>(relaxed = true)
    val jacocoModule = mockk<AppJacocoModule>(relaxed = true)
    val lintModule = mockk<ApplicationLintOptionsModule>(relaxed = true)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        val fileManager = FileManager(tmpFolder)

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.APP), projects, fileManager)
        basePlugin.apply(root)

        mockkObject(ModuleProvider)

        every { ModuleProvider.provideAppAndroidModules() } returns listOf(lintableModule, jacocoModule, lintModule)

        moduleConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the LintableModule Module`() {
        verify { lintableModule.configure(any()) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the AppJacocoModule Module`() {
        verify { jacocoModule.configure(any()) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the ApplicationLintOptionsModule Module`() {
        verify { lintModule.configure(any()) }
    }
}
