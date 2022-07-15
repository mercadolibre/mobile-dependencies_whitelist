package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
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
import com.mercadolibre.android.gradle.library.core.action.modules.lint.LibraryLintModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class LintableModuleTest : AbstractPluginManager() {

    private val lintableModule = LibraryLintModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        lintableModule.getVariants(projects[LIBRARY_PROJECT]!!)
        lintableModule.configureVariants(projects[LIBRARY_PROJECT]!!)
        lintableModule.configure(projects[LIBRARY_PROJECT]!!)

        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
    }

    @org.junit.Test
    fun `When the LintableModule configure the module variants`() {
        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            this.enabled = true
        }

        lintableModule.configureVariants(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LintableModule configure the project create her task`() {
        projects[LIBRARY_PROJECT]!!.tasks.replace(LifecycleBasePlugin.CHECK_TASK_NAME)
        lintableModule.setUpLint(projects[LIBRARY_PROJECT]!!)
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(LINTABLE_TASK) != null)
    }
}
