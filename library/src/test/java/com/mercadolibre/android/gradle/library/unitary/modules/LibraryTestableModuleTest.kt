package com.mercadolibre.android.gradle.library.unitary.modules

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.library.core.action.modules.testeable.LibraryTestableModule
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryTestableModuleTest: AbstractPluginManager() {

    val testableModule = LibraryTestableModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        BasePlugin().apply(root)
        PluginConfigurer(LIBRARY_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)

        findExtension<BaseExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            productFlavors.register("anyProductFlavor")
        }

        testableModule.configure(projects[LIBRARY_PROJECT]!!)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
    }

    @org.junit.Test
    fun `When the LibraryTestableModule is called configures the project`() {
        projects[LIBRARY_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest")
        projects[LIBRARY_PROJECT]!!.tasks.register("jacocoTestAnyProductFlavoranyNameUnitTestReport")
        projects[LIBRARY_PROJECT]!!.tasks.register("testUnitTest")
        projects[LIBRARY_PROJECT]!!.tasks.register("jacocoTestUnitTestReport")

        testableModule.configure(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("testUnitTest") != null)
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("jacocoTestUnitTestReport") != null)
    }

    @org.junit.Test
    fun `When the LibraryTestableModule is called configures the project witouth generic tasks`() {
        projects[LIBRARY_PROJECT]!!.tasks.register("testAnyProductFlavoranyNameUnitTest")
        projects[LIBRARY_PROJECT]!!.tasks.register("jacocoTestAnyProductFlavoranyNameUnitTestReport")

        testableModule.configure(projects[LIBRARY_PROJECT]!!)

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("testUnitTest") != null)
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("jacocoTestUnitTestReport") != null)
    }
}