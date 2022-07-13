package com.mercadolibre.android.gradle.app.unitary.modules.lint

import com.mercadolibre.android.gradle.app.core.action.modules.lint.AppLintModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ReleaseDependenciesLint
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

@RunWith(JUnit4::class)
class AppLintModuleTest : AbstractPluginManager() {

    private val lintModule = AppLintModule()

        @org.junit.Before
    fun setUp() {
        initTmpFolder()

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        PluginConfigurer(APP_PLUGINS).configureProject(projects[APP_PROJECT]!!)
        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)
        findExtension<LintGradleExtension>(projects[APP_PROJECT]!!)?.apply {
            this.enabled = true
        }
    }

    @org.junit.Test
    fun `When the AppLintModule is called setup`() {
        val project = mockk<Project>(relaxed = true) {
            every { tasks.names.contains(LifecycleBasePlugin.CHECK_TASK_NAME) } returns false
        }
        lintModule.setUpLint(project)
    }

    @org.junit.Test
    fun `When the AppLintModule is created work`() {
        lintModule.getVariants(projects[APP_PROJECT]!!)
        assert(lintModule.getLinter()::class == ReleaseDependenciesLint::class)
    }
}
