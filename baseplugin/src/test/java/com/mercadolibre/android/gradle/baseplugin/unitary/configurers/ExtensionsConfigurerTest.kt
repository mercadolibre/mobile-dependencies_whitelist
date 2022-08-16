package com.mercadolibre.android.gradle.baseplugin.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionsConfigurerTest : AbstractPluginManager() {

    private val extensionsConfigurer = ExtensionsConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        extensionsConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the ExtensionsConfigurer create all extensions`() {
        val extensionsNames = arrayListOf<String>()

        for (module in ModuleProvider.provideAllModules()) {
            extensionsNames.add(module.getExtensionName())
        }

        for (extensionName in extensionsNames) {
            assert(root.extensions.findByName(extensionName) != null)
        }
    }
}
