package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginConfigurerTest: AbstractPluginManager() {

    private val pluginConfigurer = PluginConfigurer(APP_PLUGINS)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
    }

    @org.junit.Test
    fun `When the AppPluginConfigurer configures a project apply the Application plugin`() {
        val projects = mutableMapOf<String, Project>()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        pluginConfigurer.configureProject(projects[APP_PROJECT]!!)

        assert(projects[APP_PROJECT]!!.plugins.findPlugin(APP_PLUGINS[1]) != null)
    }

    @org.junit.Test
    fun `When the AppPluginConfigurer configures a project apply the Kotlin plugin`() {
        val projects = mutableMapOf<String, Project>()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        pluginConfigurer.configureProject(projects[APP_PROJECT]!!)

        assert(projects[APP_PROJECT]!!.plugins.findPlugin(APP_PLUGINS[0]) != null)
    }


}