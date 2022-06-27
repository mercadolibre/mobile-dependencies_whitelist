package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.modules.plugin_description.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGIN_DESCRIPTION_TASK
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppPluginDescriptionModuleTest: AbstractPluginManager() {

    val pluginDescription = AppPluginDescriptionModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(APP_PROJECT to ModuleType.APP), projects, fileManager)

    }

    @org.junit.Test
    fun `When the AppPluginDescriptionModule configures the project craete the Plugin Description Task`() {
        pluginDescription.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(APP_PLUGIN_DESCRIPTION_TASK) != null)
    }

}