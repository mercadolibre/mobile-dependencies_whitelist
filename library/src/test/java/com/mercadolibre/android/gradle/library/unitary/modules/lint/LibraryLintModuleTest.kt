package com.mercadolibre.android.gradle.library.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.library.core.action.modules.lint.LibraryLintModule
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.utils.domain.ModuleType
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryLintModuleTest : AbstractPluginManager() {

    private val lintModule = LibraryLintModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        PluginConfigurer(LIBRARY_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)
        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
    }

    @org.junit.Test
    fun `When the LibraryLintModule is called setup`() {
        val project = mockk<Project>(relaxed = true) {
            every { tasks.names.contains(LifecycleBasePlugin.CHECK_TASK_NAME) } returns false
        }
        lintModule.setUpLint(project)
    }

    @org.junit.Test
    fun `When the LibraryLintModule is created work`() {
        assert(lintModule.getLinter(projects[LIBRARY_PROJECT]!!)::class == LibraryAllowListDependenciesLint::class)
    }
}
