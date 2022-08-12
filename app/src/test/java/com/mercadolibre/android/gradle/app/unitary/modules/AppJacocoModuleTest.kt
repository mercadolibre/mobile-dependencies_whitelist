package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class AppJacocoModuleTest : AbstractPluginManager() {

    private val jacocoModule = AppJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)

        projects[APP_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest", Test::class.java)

        jacocoModule.configure(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the JavaJacocoModule is called before evaluate execute her configuration`() {
        val project = mockk<Project>(relaxed = true)

        jacocoModule.moduleConfiguration(project)

        verify { project.tasks.named(JACOCO_FULL_REPORT_TASK) }
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule configures all the Variants tasks`() {
        jacocoModule.configVariantsTasks(projects[APP_PROJECT]!!, mockk(relaxed = true))
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
