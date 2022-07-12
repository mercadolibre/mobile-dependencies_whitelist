package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PomUtils
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_FLAVOR
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import groovy.util.Node
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import java.io.File
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PomUtilsTest: AbstractPluginManager() {

    val pomUtils = PomUtils()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.After
    fun after() {
        unmockkAll()
    }

    @org.junit.Test
    fun `PomUtils addExclusion`() {
        val dependency = mockk<ModuleDependency>()
        val node = mockk<Node>(relaxed = true)

        every { dependency.name } returns "name"
        every { dependency.group } returns "group"
        every { dependency.version } returns "version"
        every { dependency.excludeRules } returns setOf(mockk(relaxed = true))
        every { dependency.version } returns "version"

        pomUtils.addExclusions(node, dependency)
    }


    @org.junit.Test
    fun `Config with PomUtils`() {
        val dependency = mockk<Dependency>()
        val node = mockk<Node>(relaxed = true)

        every { dependency.name } returns "name"
        every { dependency.group } returns "group"
        every { dependency.version } returns "version"

        every { dependency.version } returns "version"

        pomUtils.configDependency(node, "scope", arrayListOf(), dependency)
    }

    @org.junit.Test
    fun `Config with PomUtils without dependency data`() {
        val dependency = mockk<Dependency>()
        val node = mockk<Node>(relaxed = true)

        every { dependency.group } returns null
        every { dependency.version } returns null

        every { dependency.version } returns "version"

        pomUtils.configDependency(node, "scope", arrayListOf(), dependency)
    }

    @org.junit.Test
    fun `Inject dependencies with PomUtils`() {
        val configuration = mockk<Configuration>(relaxed = true)

        val xmlProvider = mockk<XmlProvider>(relaxed = true)
        val node = mockk<Node>(relaxed = true)

        every { xmlProvider.asNode() } returns node
        every { configuration.name } returns "default"

        projects[LIBRARY_PROJECT]!!.configurations.add(configuration)

        pomUtils.injectDependencies(projects[LIBRARY_PROJECT]!!, xmlProvider, ANY_NAME, ANY_FLAVOR)
    }

    @org.junit.Test
    fun `Inject dependencies with PomUtils without parameters`() {
        val configuration = mockk<Configuration>(relaxed = true)

        val xmlProvider = mockk<XmlProvider>(relaxed = true)
        val node = mockk<Node>(relaxed = true)

        every { xmlProvider.asNode() } returns node
        every { configuration.name } returns "default"

        projects[LIBRARY_PROJECT]!!.configurations.add(configuration)

        pomUtils.injectDependencies(projects[LIBRARY_PROJECT]!!, xmlProvider, ANY_NAME, null)
    }

    @org.junit.Test
    fun `Inject dependencies with PomUtils for a configuration without valid name`() {
        val configuration = mockk<Configuration>(relaxed = true)

        val xmlProvider = mockk<XmlProvider>()
        val node = mockk<Node>(relaxed = true)

        every { xmlProvider.asNode() } returns node
        every { configuration.name } returns ""

        projects[LIBRARY_PROJECT]!!.configurations.add(configuration)

        pomUtils.injectDependencies(projects[LIBRARY_PROJECT]!!, xmlProvider, ANY_NAME, null)
    }

}