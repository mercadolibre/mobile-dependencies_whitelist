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
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListVariantsTest: AbstractPluginManager() {

    private val listVariants = ListVariantsModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

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