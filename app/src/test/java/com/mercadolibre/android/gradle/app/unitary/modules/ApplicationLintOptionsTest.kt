package com.mercadolibre.android.gradle.app.unitary.modules

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager

import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import java.io.File
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ApplicationLintOptionsTest: AbstractPluginManager() {

    private val basePlugin = BasePlugin()
    private val appPlugin = BaseAppPlugin()
    private val appLintOptions = ApplicationLintOptionsModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

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