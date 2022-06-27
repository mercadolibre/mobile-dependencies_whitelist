package com.mercadolibre.android.gradle.app.unitary.modules

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ApplicationLintOptionsTest: AbstractPluginManager() {

    val basePlugin = BasePlugin()
    val appPlugin = BaseAppPlugin()
    val appLintOptions = ApplicationLintOptionsModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(APP_PROJECT to ModuleType.APP), projects, fileManager)

        basePlugin.apply(root)
        appPlugin.apply(projects[APP_PROJECT]!!)

        appLintOptions.configure(projects[APP_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the ApplicationLintOptionsModule configures the project set IsCheckDependencies True`() {
        findExtension<BaseExtension>(projects[APP_PROJECT]!!)?.apply {
            assert(lintOptions.isCheckDependencies)
        }
    }

}