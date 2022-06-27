package com.mercadolibre.android.gradle.library.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import org.gradle.api.Project
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginConfigurerTest: AbstractPluginManager() {

    val pluginConfigurer = PluginConfigurer(LIBRARY_PLUGINS)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
    }

    @org.junit.Test
    fun `When the LibraryPluginConfigurer configures a project apply the Library plugin`() {
        val fileManager = FileManager(tmpFolder)
        val projects = mutableMapOf<String, Project>()
        root = moduleManager.createRootProject("$ROOT_PROJECT-1", mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(LIBRARY_PLUGINS[1]) != null)
    }

    @org.junit.Test
    fun `When the LibraryPluginConfigurer configures a project apply the Kotlin plugin`() {
        val fileManager = FileManager(tmpFolder)
        val projects = mutableMapOf<String, Project>()
        root = moduleManager.createRootProject("$ROOT_PROJECT-2", mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(LIBRARY_PLUGINS[0]) != null)
    }


}