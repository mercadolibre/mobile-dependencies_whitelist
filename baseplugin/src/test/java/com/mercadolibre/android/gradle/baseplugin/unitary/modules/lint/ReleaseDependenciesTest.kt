package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.ReleaseDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.BaseLibraryPlugin
import groovy.util.Node
import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.gradle.api.artifacts.Dependency
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReleaseDependenciesTest: AbstractPluginManager() {

    private val libraryConfigurer = BaseLibraryPlugin()
    private val releaseDependencies = ReleaseDependenciesLint()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        libraryConfigurer.apply(projects[LIBRARY_PROJECT]!!)
        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)

        releaseDependencies.name()

        val configuration = projects[LIBRARY_PROJECT]!!.configurations.create(ANY_NAME)

        val dependency = mockk<Dependency>()

        every { dependency.name } returns "name"
        every { dependency.group } returns "group"
        every { dependency.version } returns PUBLISHING_EXPERIMENTAL

        every { dependency.version } returns "version"

        configuration.dependencies.add(dependency)

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            this.enabled = true
            this.dependenciesLintEnabled = true
            this.releaseDependenciesLintEnabled = true
        }
    }

    @org.junit.Test
    fun `When the ReleaseDependenciesLint lint excute right`() {
        val variant = mockVariant()

        releaseDependencies.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the ReleaseDependenciesLint is called check is exist any fail`() {
        val file = mockk<File>(relaxed = true)

        every { file.parentFile.mkdirs() } returns mockk(relaxed = true)
        every { file.exists() } returns mockk(relaxed = true)
        every { file.path } returns "./build/tmp/asd.txt"

        releaseDependencies.checkIsFailed(listOf(ANY_NAME).stream(), file)
    }

}