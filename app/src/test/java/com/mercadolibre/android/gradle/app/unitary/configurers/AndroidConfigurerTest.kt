package com.mercadolibre.android.gradle.app.unitary.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.app.core.action.configurers.AppAndroidConfigurer
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS

class AndroidConfigurerTest : AbstractPluginManager() {

    private val androidConfigurer = AppAndroidConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        androidConfigurer.getDescription()
        PluginConfigurer(APP_PLUGINS).configureProject(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AppAndroidConfigurer is called configure the ProguardFiles`() {
        // Not exist Proguard Files
        findExtension<BaseExtension>(projects[APP_PROJECT]!!)?.apply {
            assert(defaultConfig.proguardFiles.isEmpty())
        }

        // Add Proguard Files
        androidConfigurer.configureProject(projects[APP_PROJECT]!!)

        // Exist Proguard Files
        findExtension<BaseExtension>(projects[APP_PROJECT]!!)?.apply {
            assert(defaultConfig.proguardFiles.isNotEmpty())
        }
    }
}
