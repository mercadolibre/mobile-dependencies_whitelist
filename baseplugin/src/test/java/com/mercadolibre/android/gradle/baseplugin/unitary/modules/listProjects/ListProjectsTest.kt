package com.mercadolibre.android.gradle.baseplugin.unitary.modules.listProjects

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import java.io.File
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ListProjectsTest: AbstractPluginManager() {

    private val listProjects = ListProjectsModule()


    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.Test
    fun `When the ListProjectsModule is called configure the project`() {
        listProjects.printProjects(root)
    }

}