package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN_DESCRIPTION_TASK
import com.mercadolibre.android.gradle.library.core.action.modules.plugin_description.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryPluginDescriptionModuleTest: AbstractPluginManager() {

    val pluginDescription = LibraryPluginDescriptionModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        pluginDescription.configure(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AppPluginDescriptionModule configures the project craete the Plugin Description Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(LIBRARY_PLUGIN_DESCRIPTION_TASK) != null)
    }

}