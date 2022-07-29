package com.mercadolibre.android.gradle.dynamicfeature.unitary.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.DYNAMIC_FEATURE_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider
import com.mercadolibre.android.gradle.dynamicfeature.core.action.configurers.DynamicFeatureAndroidConfigurer
import com.mercadolibre.android.gradle.dynamicfeature.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.dynamicfeature.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.dynamicfeature.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DynamicFeaturesAndroidConfigurerTest : AbstractPluginManager() {

    private val androidConfigurer = DynamicFeatureAndroidConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        PluginConfigurer(DYNAMIC_FEATURE_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)
        androidConfigurer.configureProject(projects[LIBRARY_PROJECT]!!)
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
