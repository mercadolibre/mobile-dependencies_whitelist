package com.mercadolibre.android.gradle.library.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.core.action.modules.testeable.LibraryTestableModule
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class ModuleConfigurerTest : AbstractPluginManager() {

    private val basePlugin = BasePlugin()

    private val moduleConfigurer = LibraryModuleConfigurer()

    private val lintableModule = mockk<LintableModule>(relaxed = true)
    private val jacocoModule = mockk<LibraryJacocoModule>(relaxed = true)
    private val testableModule = mockk<LibraryTestableModule>(relaxed = true)
    private val publishModule = mockk<LibraryPublishableModule>(relaxed = true)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        basePlugin.apply(root)

        mockkObject(ModuleProvider)

        every { ModuleProvider.provideLibraryAndroidModules() } returns listOf(lintableModule, jacocoModule, testableModule, publishModule)

        moduleConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the LintableModule Module`() {
        verify { lintableModule.configure(projects[LIBRARY_PROJECT]!!) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the LibraryJacocoModule Module`() {
        verify { jacocoModule.configure(projects[LIBRARY_PROJECT]!!) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the LibraryTestableModule Module`() {
        verify { testableModule.configure(projects[LIBRARY_PROJECT]!!) }
    }

    @org.junit.Test
    fun `When the ModuleConfigurer configures a execute the LibraryPublishableModule Module`() {
        verify { publishModule.configure(projects[LIBRARY_PROJECT]!!) }
    }
}
