package com.mercadolibre.android.gradle.baseplugin.integration.utils

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.integration.plugins_tests.PluginBaseTest
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import java.io.File

abstract class UtilsTest: AbstractPluginManager() {

    val basePlugin = BasePlugin()

    abstract fun getProjectsName(): MutableMap<String, ModuleType>

    @org.junit.Before
    fun init() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, getProjectsName(), projects, fileManager)
        basePlugin.apply(root)
    }

    /*****************************************
     *                  TEST                  *
     *******************************************/

    @org.junit.Test
    fun `The build should be Successfully when the root project is compiled`() {
        val gradle = runGradle(tmpFolder.root)
        assert(gradle.output.contains("BUILD SUCCESSFUL"))
    }

    @org.junit.Test
    fun `The build must be published after being compiled`() {
        val gradle = runGradle(tmpFolder.root)
        assert(gradle.output.contains("https://gradle.adminml.com/s/"))
    }

    @org.junit.Test
    fun `Jacoco tasks must be created when applying the Settings Plugin`() {
        PluginBaseTest.jacocoTasks(root)
    }

    @org.junit.Test
    fun `Project Version task must be created when applying the Settings Plugin`() {
        PluginBaseTest.getProjectVersionTask(root)
    }

    @org.junit.Test
    fun `List Projects task must be created when applying the Settings Plugin`() {
        PluginBaseTest.listProjectsTask(root)
    }

    @org.junit.Test
    fun `Plugin Description task must be created when applying the Settings Plugin`() {
        PluginBaseTest.pluginDescriptionTask(root)
    }
}