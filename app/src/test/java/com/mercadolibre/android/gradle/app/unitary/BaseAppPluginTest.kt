package com.mercadolibre.android.gradle.app.unitary

import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
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

    val appPlugin = BaseAppPlugin()

    val pluginConfigurer: PluginConfigurer = mock {}
    val androidConfigurer: AndroidConfigurer = mock {}
    val moduleConfigurer: AppModuleConfigurer = mock {}

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        appPlugin.configurers.clear()
        appPlugin.configurers.addAll(listOf(pluginConfigurer, androidConfigurer, moduleConfigurer))

        appPlugin.apply(projects[LIBRARY_PROJECT]!!)
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