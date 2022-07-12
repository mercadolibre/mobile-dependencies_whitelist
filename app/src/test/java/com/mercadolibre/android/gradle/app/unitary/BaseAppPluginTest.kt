package com.mercadolibre.android.gradle.app.unitary

import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BaseAppPluginTest: AbstractPluginManager() {

    private val appPlugin = BaseAppPlugin()

    private val pluginConfigurer: PluginConfigurer = mock {}
    private val androidConfigurer: AndroidConfigurer = mock {}
    private val moduleConfigurer: AppModuleConfigurer = mock {}

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        appPlugin.configurers.clear()
        appPlugin.configurers.addAll(listOf(pluginConfigurer, androidConfigurer, moduleConfigurer))

        appPlugin.apply(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the App is applied the PluginConfigurer configures the project`() {
        verify(pluginConfigurer).configureProject(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the App is applied the AndroidConfigurer configures the project`() {
        verify(androidConfigurer).configureProject(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the App is applied the ModuleConfigurer configures the project`() {
        verify(moduleConfigurer).configureProject(projects[APP_PROJECT]!!)
    }
 }