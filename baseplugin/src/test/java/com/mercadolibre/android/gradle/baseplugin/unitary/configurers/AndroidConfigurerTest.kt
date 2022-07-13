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
    private val androidConfigurer = AndroidConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
        
        basePlugin.apply(root)
        androidConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Compile Sdk Version`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(compileSdkVersion == VersionProvider.provideApiSdkLevel().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Build Tools Version`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(buildToolsVersion == VersionProvider.provideBuildToolsVersion())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Min Sdk Version`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.minSdkVersion!!.apiString == VersionProvider.provideMinSdk().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Target Sdk Version`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.targetSdkVersion!!.apiString == VersionProvider.provideApiSdkLevel().toString())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Source Compatibility`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(compileOptions.sourceCompatibility == VersionProvider.provideJavaVersion())
        }
    }

    @org.junit.Test
    fun `When the AndroidConfigurer configures a project set the Target Compatibility`() {
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(compileOptions.targetCompatibility == VersionProvider.provideJavaVersion())
        }
    }
}
