package com.mercadolibre.android.gradle.library.integration.utils

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.library.BaseLibraryPlugin
import com.mercadolibre.android.gradle.library.integration.plugins_tests.PluginBaseTest
import com.mercadolibre.android.gradle.library.integration.plugins_tests.PluginLibraryTest
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ModuleManager
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import java.io.File

abstract class UtilsTest: AbstractPluginManager() {

    val basePlugin = BasePlugin()
    val libraryPlugin = BaseLibraryPlugin()

    abstract fun getProjectsName(): MutableMap<String, ModuleType>

    @org.junit.Before
    fun init() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)
        val moduleManager = ModuleManager()

        pathsAffectingAllModules.forEach { File(tmpDir, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, getProjectsName(), projects, fileManager)
        basePlugin.apply(root)
    }

    /*****************************************
     *                  TEST                  *
     *******************************************/

    @org.junit.Test
    fun `Jacoco tasks must be created when applying the Settings Plugin`() {
        for (project in projects) {
            if (project.key == LIBRARY_PROJECT) {
                libraryPlugin.apply(project.value)
                PluginBaseTest.jacocoTasks(project.value)
            }
        }
    }

    @org.junit.Test
    fun `Lint tasks must be created when applying the Settings Plugin`() {
        for (project in projects) {
            if (project.key == LIBRARY_PROJECT) {
                libraryPlugin.apply(project.value)
                PluginBaseTest.lintTasks(project.value)
            }
        }
    }

    @org.junit.Test
    fun `Publishing tasks must be created when applying the Settings Plugin`() {
        for (project in projects) {
            if (project.key == LIBRARY_PROJECT) {
                libraryPlugin.apply(project.value)
                PluginLibraryTest.publishTasks(project.value)
            }
        }
    }
}