package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import org.gradle.api.Project
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginConfigurerTest: AbstractPluginManager() {

    val pluginConfigurer = PluginConfigurer(APP_PLUGINS)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
    }

    @org.junit.Test
    fun `When the AppPluginConfigurer configures a project apply the Application plugin`() {
        val fileManager = FileManager(tmpFolder)
        val projects = mutableMapOf<String, Project>()
        root = moduleManager.createRootProject("root1", mutableMapOf(LIBRARY_PROJECT to ModuleType.APP), projects, fileManager)
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(APP_PLUGINS[1]) != null)
    }

    @org.junit.Test
    fun `When the AppPluginConfigurer configures a project apply the Kotlin plugin`() {
        val fileManager = FileManager(tmpFolder)
        val projects = mutableMapOf<String, Project>()
        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(APP_PLUGINS[0]) != null)
    }


}