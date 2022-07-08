package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import org.gradle.api.tasks.testing.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class LibraryJacocoModuleTest : AbstractPluginManager() {

    val jacocoModule = LibraryJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)

        projects[LIBRARY_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest", Test::class.java)

        jacocoModule.configure(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AppJacocoModule configures the project create JacocoFullReport Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(JACOCO_FULL_REPORT_TASK) != null)
    }

    @org.junit.Test
    fun `When the AppJacocoModule configures the project create JacocoTestReport Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(JACOCO_TEST_REPORT_TASK) != null)
    }
}
