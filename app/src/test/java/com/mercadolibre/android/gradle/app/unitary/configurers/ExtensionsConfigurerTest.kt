package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.core.action.configurers.AppExtensionConfigurer
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionsConfigurerTest : AbstractPluginManager() {

    private val extensionsConfigurer = AppExtensionConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        extensionsConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the ExtensionsConfigurer create all extensions`() {

        val extensionsNames = arrayListOf<String>()

        for (module in ModuleProvider.provideAppAndroidModules()) {
            extensionsNames.add(module.getExtensionName())
        }

        for (extensionName in extensionsNames) {
            assert(root.extensions.findByName(extensionName) != null)
        }
    }
}
