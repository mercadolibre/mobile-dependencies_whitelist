package com.mercadolibre.android.gradle.baseplugin.unitary.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider
import org.gradle.api.Project
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.kotlin.dsl.apply
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AndroidConfigurerTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()

    val androidConfigurer = AndroidConfigurer()

    lateinit var subProject: Project

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        subProject = moduleManager.createSampleSubProject("p", tmpFolder, root)
        basePlugin.apply(root)
        androidConfigurer.configureProject(subProject)
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Compile Sdk Version`() {
        findExtension<BaseExtension>(subProject)?.apply {
            assert(compileSdkVersion == VersionProvider.provideApiSdkLevel().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Build Tools Version`() {
        findExtension<BaseExtension>(subProject)?.apply {
            assert(buildToolsVersion == VersionProvider.provideBuildToolsVersion())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Min Sdk Version`() {
        val newTempFolder = TemporaryFolder()
        newTempFolder.create()

        val fileManager = FileManager(newTempFolder)
        val projects = mutableMapOf<String, Project>()
        val root = moduleManager.createRootProject("rootSample", mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        root.apply(plugin = "mercadolibre.gradle.config.settings")
        projects[LIBRARY_PROJECT]!!.apply(plugin = "mercadolibre.gradle.config.library")

        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.minSdkVersion!!.apiString == VersionProvider.provideMinSdk().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Target Sdk Version`() {
        val newTempFolder = TemporaryFolder()
        newTempFolder.create()

        val fileManager = FileManager(newTempFolder)
        val projects = mutableMapOf<String, Project>()
        val root = moduleManager.createRootProject("rootSample", mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        root.apply(plugin = "mercadolibre.gradle.config.settings")
        projects[LIBRARY_PROJECT]!!.apply(plugin = "mercadolibre.gradle.config.library")

        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {

            assert(defaultConfig.targetSdkVersion!!.apiString == VersionProvider.provideApiSdkLevel().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Source Compatibility`() {
        findExtension<BaseExtension>(subProject)?.apply {
            assert(compileOptions.sourceCompatibility == VersionProvider.provideJavaVersion())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Target Compatibility`() {
        findExtension<BaseExtension>(subProject)?.apply {
            assert(compileOptions.targetCompatibility == VersionProvider.provideJavaVersion())
        }
    }
}
