package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.JavaLintModule
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.mockk.Called
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LintableModuleTest : AbstractPluginManager() {

    private val lintableModule = JavaLintModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        lintableModule.configureVariants(projects[LIBRARY_PROJECT]!!)
        lintableModule.configure(projects[LIBRARY_PROJECT]!!)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)
    }

    @org.junit.Test
    fun `When the LintableModule is called check her extension and response because all flags are true`() {
        val project = mockk<Project>(relaxed = true)
        val extension = LintGradleExtension()

        lintableModule.createExtension(project)

        lintableModule.executeModule(project)

        every { project.extensions.findByName(LINTABLE_EXTENSION) } returns extension

        verify { project.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java) }
        verify { project.extensions.findByName(LINTABLE_EXTENSION) }

        // Project call lint in After Evaluate
        verify { project.afterEvaluate(any<Action<Project>>()) }
    }

    @org.junit.Test
    fun `When the LintableModule is called check her extension and response because not exist`() {
        val project = mockk<Project>(relaxed = true)

        lintableModule.executeModule(project)

        verify { project.extensions.findByName(LINTABLE_EXTENSION) }
    }

    @org.junit.Test
    fun `When the LintableModule is called check her extension and not response because flags are False`() {
        val project = mockk<Project>(relaxed = true)
        val extension = LintGradleExtension()
        extension.dependenciesLintEnabled = false

        lintableModule.createExtension(project)

        every { project.extensions.findByName(LINTABLE_EXTENSION) } returns extension

        lintableModule.executeModule(project)

        verify { project.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java) }
        verify { project.extensions.findByName(LINTABLE_EXTENSION) }

        //Print warning message with project.name
        verify { project.name }

        // Project not call lint in After Evaluate
        verify(exactly = 0) { project.afterEvaluate(any<Action<Project>>()) }
    }

    @org.junit.Test
    fun `When the LintableModule is called create the extension`() {
        lintableModule.createExtension(projects[LIBRARY_PROJECT]!!)

        assert(lintableModule.getExtensionName() == LINTABLE_EXTENSION)
        assert(projects[LIBRARY_PROJECT]!!.extensions.findByName(LINTABLE_EXTENSION) != null)
    }

    @org.junit.Test
    fun `When the LintableModule configure the module variants`() {
        lintableModule.configureVariants(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LintableModule configure the project create her task`() {
        projects[LIBRARY_PROJECT]!!.tasks.replace(LifecycleBasePlugin.CHECK_TASK_NAME)
        lintableModule.setUpLint(projects[LIBRARY_PROJECT]!!)
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(LINTABLE_TASK) != null)
    }
}
