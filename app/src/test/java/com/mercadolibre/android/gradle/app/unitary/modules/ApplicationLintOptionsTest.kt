package com.mercadolibre.android.gradle.app.unitary.modules

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import org.gradle.api.Project

@RunWith(JUnit4::class)
class ApplicationLintOptionsTest : AbstractPluginManager() {

    private val appLintOptions = ApplicationLintOptionsModule()


    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }
    }

    @org.junit.Test
    fun `When the ApplicationLintOptionsModule configures the project set IsCheckDependencies True`() {
        val project = mockk<Project>(relaxed = true)
        val extension = mockk<BaseExtension>(relaxed = true)

        every { project.extensions.findByType(BaseExtension::class.java) } returns extension

        appLintOptions.configure(project)
        appLintOptions.configureLintOptions(project)

        verify { extension.lintOptions(any()) }
    }
}
