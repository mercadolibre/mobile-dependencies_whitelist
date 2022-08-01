package com.mercadolibre.android.gradle.library.unitary.configurers

import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryExtensionConfigurer
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExtensionsConfigurerTest : AbstractPluginManager() {

    private val extensionsConfigurer = LibraryExtensionConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        extensionsConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the ExtensionsConfigurer create all extensions`() {

        val extensionsNames = arrayListOf<String>()

        for (module in ModuleProvider.provideLibraryAndroidModules()) {
            extensionsNames.add(module.getExtensionName())
        }

        for (extensionName in extensionsNames) {
            assert(root.extensions.findByName(extensionName) != null)
        }
    }
}
