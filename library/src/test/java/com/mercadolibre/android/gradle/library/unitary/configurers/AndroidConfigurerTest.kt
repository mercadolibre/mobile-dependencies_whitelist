package com.mercadolibre.android.gradle.library.unitary.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryAndroidConfigurer
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT

class AndroidConfigurerTest : AbstractPluginManager() {

    private val androidConfigurer = LibraryAndroidConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        androidConfigurer.getDescription()
        PluginConfigurer(APP_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryAndroidConfigurer is called configure the ProguardFiles`() {
        // Not exist Proguard Files
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.proguardFiles.isEmpty())
        }

        // Add Proguard Files
        androidConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        // Exist Proguard Files
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.proguardFiles.isNotEmpty())
        }
    }

    @org.junit.Test
    fun `When the LibraryAndroidConfigurer is called configure the ConsumerProguard`() {
        // Not exist Proguard Files
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.consumerProguardFiles.isEmpty())
        }

        // Add Proguard Files
        androidConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)

        // Exist Proguard Files
        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            assert(defaultConfig.consumerProguardFiles.isNotEmpty())
        }
    }
}
