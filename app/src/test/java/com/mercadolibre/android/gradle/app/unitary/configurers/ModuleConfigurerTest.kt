package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class ModuleConfigurerTest : AbstractPluginManager() {

    private val basePlugin = BasePlugin()

    private val moduleConfigurer = AppModuleConfigurer()

    private val lintableModule = mockk<LintableModule>(relaxed = true)
    private val jacocoModule = mockk<AppJacocoModule>(relaxed = true)
    private val lintModule = mockk<ApplicationLintOptionsModule>(relaxed = true)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        basePlugin.apply(root)

        mockkObject(ModuleProvider)

        every { ModuleProvider.provideAppAndroidModules() } returns listOf(lintableModule, jacocoModule, lintModule)

        moduleConfigurer.configureProject(projects[APP_PROJECT]!!)
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
