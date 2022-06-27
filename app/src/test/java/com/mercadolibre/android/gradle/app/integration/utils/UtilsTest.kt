package com.mercadolibre.android.gradle.app.integration.utils

import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.integration.plugins_tests.PluginBaseTest
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.ModuleManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.managers.TEST_APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import java.io.File

abstract class UtilsTest: AbstractPluginManager() {

    val basePlugin = BasePlugin()
    val appPlugin = BaseAppPlugin()

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
    fun `Jacoco tasks must be created when applying the App Plugin`() {
        for (project in projects) {
            if (project.key == APP_PROJECT || project.key == TEST_APP_PROJECT) {
                appPlugin.apply(project.value)
                PluginBaseTest.jacocoTasks(project.value)
            }
        }
    }

    @org.junit.Test
    fun `Lint tasks must be created when applying the App Plugin`() {
        for (project in projects) {
            if (project.key == APP_PROJECT || project.key == TEST_APP_PROJECT) {
                appPlugin.apply(project.value)
                PluginBaseTest.lintTasks(project.value)
            }
        }
    }

}