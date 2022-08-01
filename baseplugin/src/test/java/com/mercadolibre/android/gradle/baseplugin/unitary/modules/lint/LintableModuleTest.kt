package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.core.action.modules.lint.LibraryLintModule
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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

        lintableModule.createExtension(projects[LIBRARY_PROJECT]!!)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
    }

    @org.junit.Test
    fun `When the LintableModule is called create the extension`() {
        assert(lintableModule.getExtensionName() == LINTABLE_EXTENSION)
        assert(projects[LIBRARY_PROJECT]!!.extensions.findByName(LINTABLE_EXTENSION) != null)
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
