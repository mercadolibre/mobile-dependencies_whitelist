package com.mercadolibre.android.gradle.baseplugin.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionsConfigurerTest: AbstractPluginManager() {

    val basePlugin = BasePlugin()

    val extensionsConfigurer = ExtensionsConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        extensionsConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the ExtensionsConfigurer configures a project create the Jacoco Extension`() {
        assert(root.extensions.findByName(JACOCO_EXTENSION) != null)
    }

    @org.junit.Test
    fun `When the ExtensionsConfigurer configures a project create the Lint Extension`() {
        assert(root.extensions.findByName(LINTABLE_EXTENSION) != null)
    }

}