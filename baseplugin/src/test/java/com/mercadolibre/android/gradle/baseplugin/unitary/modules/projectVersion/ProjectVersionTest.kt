package com.mercadolibre.android.gradle.baseplugin.unitary.modules.projectVersion

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.project_version.ProjectVersionModule
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class ProjectVersionTest : AbstractPluginManager() {

    val projectVersion = ProjectVersionModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)
    }

    @org.junit.Test
    fun `When the ProjectVersion is called configure the project`() {
        projectVersion.printProjectVersion(root)
    }
}
