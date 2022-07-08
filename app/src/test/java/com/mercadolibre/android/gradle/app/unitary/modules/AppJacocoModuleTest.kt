package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import org.gradle.api.tasks.testing.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class AppJacocoModuleTest : AbstractPluginManager() {

    val jacocoModule = AppJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(APP_PROJECT to ModuleType.APP), projects, fileManager)

        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)

        projects[APP_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest", Test::class.java)

        jacocoModule.configure(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AppJacocoModule configures the project create JacocoFullReport Task`() {
        assert(projects[APP_PROJECT]!!.tasks.findByName(JACOCO_FULL_REPORT_TASK) != null)
    }

    @org.junit.Test
    fun `When the AppJacocoModule configures the project create JacocoTestReport Task`() {
        assert(projects[APP_PROJECT]!!.tasks.findByName(JACOCO_TEST_REPORT_TASK) != null)
    }
}
