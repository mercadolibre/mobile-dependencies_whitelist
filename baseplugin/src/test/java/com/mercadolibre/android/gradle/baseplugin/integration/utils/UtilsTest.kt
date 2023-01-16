package com.mercadolibre.android.gradle.baseplugin.integration.utils

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import org.gradle.testkit.runner.BuildResult
import java.io.File

abstract class UtilsTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()

    abstract fun getProjectsName(): MutableMap<String, ModuleType>

    lateinit var gradle: BuildResult

    @org.junit.Before
    fun init() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, getProjectsName(), projects, fileManager)

        gradle = runGradle(tmpFolder.root)
    }

    /*****************************************
     *                  TEST                  *
     *******************************************/

    @org.junit.Test
    fun `The build should be Successfully when the root project is compiled`() {
        assert(gradle.output.contains("BUILD SUCCESSFUL"))
    }

    @org.junit.Test
    fun `The build must be published after being compiled`() {
        assert(gradle.output.contains("Publishing build scan...") )
    }
}
