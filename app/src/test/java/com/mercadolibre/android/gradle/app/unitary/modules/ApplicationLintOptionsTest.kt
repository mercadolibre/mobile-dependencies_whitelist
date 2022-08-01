package com.mercadolibre.android.gradle.app.unitary.modules

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.app.BaseAppPlugin
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager

import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import java.io.File
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ApplicationLintOptionsTest: AbstractPluginManager() {

    private val appPlugin = BaseAppPlugin()
    private val appLintOptions = ApplicationLintOptionsModule()

    @org.junit.Test
    fun `When the ApplicationLintOptionsModule configures the project set IsCheckDependencies True`() {
        initTmpFolder()

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        appLintOptions.configureLintOptions(projects[APP_PROJECT]!!)
        PluginConfigurer(APP_PLUGINS).configureProject(projects[APP_PROJECT]!!)
        appLintOptions.configure(projects[APP_PROJECT]!!)
        appLintOptions.configureLintOptions(projects[APP_PROJECT]!!)
    }

}