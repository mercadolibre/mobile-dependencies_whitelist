package com.mercadolibre.android.gradle.library.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginConfigurerTest: AbstractPluginManager() {

    private val pluginConfigurer = PluginConfigurer(LIBRARY_PLUGINS)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.Test
    fun `When the LibraryPluginConfigurer configures a project apply the Library plugin`() {
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(LIBRARY_PLUGINS[1]) != null)
    }

    @org.junit.Test
    fun `When the LibraryPluginConfigurer configures a project apply the Kotlin plugin`() {
        pluginConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
        assert(projects[LIBRARY_PROJECT]!!.plugins.findPlugin(LIBRARY_PLUGINS[0]) != null)
    }


}