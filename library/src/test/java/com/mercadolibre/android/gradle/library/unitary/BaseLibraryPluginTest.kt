package com.mercadolibre.android.gradle.library.unitary

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.library.BaseLibraryPlugin
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BaseLibraryPluginTest: AbstractPluginManager() {

    val libraryPlugin = BaseLibraryPlugin()

    val pluginConfigurer: PluginConfigurer = mock {}
    val androidConfigurer: AndroidConfigurer = mock {}
    val moduleConfigurer: LibraryModuleConfigurer = mock {}

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        libraryPlugin.configurers.clear()
        libraryPlugin.configurers.addAll(listOf(pluginConfigurer, androidConfigurer, moduleConfigurer))

        libraryPlugin.apply(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the Library is applied the PluginConfigurer configures the project`() {
        verify(pluginConfigurer).configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the Library is applied the AndroidConfigurer configures the project`() {
        verify(androidConfigurer).configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the Library is applied the ModuleConfigurer configures the project`() {
        verify(moduleConfigurer).configureProject(projects[LIBRARY_PROJECT]!!)
    }
}