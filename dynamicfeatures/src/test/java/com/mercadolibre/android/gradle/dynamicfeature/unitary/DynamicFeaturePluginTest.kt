package com.mercadolibre.android.gradle.dynamicfeature.unitary

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.dynamicfeature.DynamicFeaturesPlugin
import com.mercadolibre.android.gradle.dynamicfeature.core.action.configurers.DynamicFeatureAndroidConfigurer
import com.mercadolibre.android.gradle.dynamicfeature.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.dynamicfeature.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.dynamicfeature.managers.ROOT_PROJECT
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DynamicFeaturePluginTest : AbstractPluginManager() {

    private val dynamicFeaturesPlugin = DynamicFeaturesPlugin()

    private val pluginConfigurer: PluginConfigurer = mock {}
    private val androidConfigurer: DynamicFeatureAndroidConfigurer = mock {}

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        dynamicFeaturesPlugin.configurers.clear()
        dynamicFeaturesPlugin.configurers.addAll(listOf(pluginConfigurer, androidConfigurer))

        dynamicFeaturesPlugin.apply(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the Library is applied the PluginConfigurer configures the project`() {
        verify(pluginConfigurer).configureProject(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the Library is applied the ModuleConfigurer configures the project`() {
        verify(androidConfigurer).configureProject(projects[LIBRARY_PROJECT]!!)
    }
}
