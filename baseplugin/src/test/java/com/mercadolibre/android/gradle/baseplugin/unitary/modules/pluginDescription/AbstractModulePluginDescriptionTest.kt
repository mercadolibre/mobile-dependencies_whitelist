package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractModulePluginDescription
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import io.mockk.mockk
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AbstractModulePluginDescriptionTest: AbstractPluginManager() {

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        val fileManager = FileManager(tmpFolder)

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)
    }

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is created works`() {
        val pluginDescription = PluginDescriptionClassTest()

        pluginDescription.configure(root)
        pluginDescription.configure(root)

        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.makeMessage(ANY_NAME, ANY_NAME)
        pluginDescription.configureTask(mockk(relaxed = true))
    }

    class PluginDescriptionClassTest : AbstractModulePluginDescription(ANY_NAME, ANY_NAME, { ANY_NAME })
}
