package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint.java

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.JavaLintModule
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JavaLintModuleTest : AbstractPluginManager() {

    private val lintModule = JavaLintModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        PluginConfigurer(LIBRARY_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)
        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            this.enabled = true
        }
    }

    @org.junit.Test
    fun `When the LibraryLintModule is called setup`() {
        val project = mockk<Project>(relaxed = true) {
            every { tasks.names.contains(LifecycleBasePlugin.CHECK_TASK_NAME) } returns false
            every { extensions.findByType(SourceSetContainer::class.java) } returns mockk(relaxed = true)
        }
        lintModule.getLinter(project)
        lintModule.setUpLint(project)

        verify { project.extensions.findByType(SourceSetContainer::class.java) }
    }
}
