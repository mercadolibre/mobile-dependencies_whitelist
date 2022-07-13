package com.mercadolibre.android.gradle.baseplugin.unitary.modules.projectVersion

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.project_version.ProjectVersion
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import java.io.File
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectVersionTest: AbstractPluginManager() {

    private val projectVersion = ProjectVersion()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.Test
    fun `When the ProjectVersion is called configure the project`() {
        projectVersion.printProjectVersion(root)
    }

}