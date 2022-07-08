package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import java.io.File
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LintableModuleTest: AbstractPluginManager() {

    val lintableModule = LintableModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, APP_PROJECT to ModuleType.APP), projects, fileManager)

        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)

        for (projectName in listOf(LIBRARY_PROJECT, APP_PROJECT)) {
            findExtension<LintGradleExtension>(projects[projectName]!!)?.apply {
                this.enabled = true
            }
            lintableModule.setUpLint(projects[projectName]!!)
        }
    }

    @org.junit.Test
    fun `When the LintableModule configure check the dependencies`() {
        lintableModule.checkStatusLint(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LintableModule configure the project create her task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(LINTABLE_TASK) != null)
    }
}