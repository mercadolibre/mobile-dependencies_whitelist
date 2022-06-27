package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listVariants

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants.ListVariantsModule
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListVariantsTest: AbstractPluginManager() {

    val listVariants = ListVariantsModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, APP_PROJECT to ModuleType.APP), projects, fileManager)

        PluginConfigurer(APP_PLUGINS).configureProject(projects[APP_PROJECT]!!)
        PluginConfigurer(LIBRARY_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)

        listVariants.findExtension(projects[LIBRARY_PROJECT]!!, ANY_NAME)

        runGradle(tmpFolder.root)
    }

    @org.junit.Test
    fun `When the ListVariantsModule is called configure the project`() {
        listVariants.printVariants(root)
    }

}