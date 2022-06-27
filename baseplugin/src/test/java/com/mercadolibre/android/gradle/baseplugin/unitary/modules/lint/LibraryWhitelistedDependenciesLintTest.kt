package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.LibraryWhitelistedDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.BaseLibraryPlugin
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryWhitelistedDependenciesLintTest: AbstractPluginManager() {

    val libraryConfigurer = BaseLibraryPlugin()
    val lintableModule = LibraryWhitelistedDependenciesLint()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        libraryConfigurer.apply(projects[LIBRARY_PROJECT]!!)

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            this.enabled = true
            this.dependenciesLintEnabled = true
        }

        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest lint excute right`() {
        val variant = mockVariant()

        val dependency =
            Dependency(
                "group",
                "name",
                "version",
                1,
                null
            )

        lintableModule.WHITELIST_GOING_TO_EXPIRE.add(dependency)
        lintableModule.WHITELIST_DEPENDENCIES.add(dependency)

        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest is disable and lint is called`() {

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            enabled = false
            dependenciesLintEnabled = false
            dependencyWhitelistUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"
        }

        val variant = mockVariant()

        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest is called analize any Dependency without data`() {
        val dependency =
            Dependency(
                "group",
                null,
                null,
                null,
                null
            )
        lintableModule.analizeDependency(dependency, projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest is called analize any Dependency`() {
        val dependency =
            Dependency(
                "group",
                "name",
                "version",
                1,
                "2022-06-05"
            )

        lintableModule.WHITELIST_GOING_TO_EXPIRE.add(dependency)
        lintableModule.WHITELIST_DEPENDENCIES.add(dependency)

        lintableModule.analizeDependency(dependency, projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest is called analize any Dependency without expires`() {
        val dependency =
            Dependency(
                "group",
                "name",
                "version",
                null,
                "2022-06-05"
            )

        lintableModule.WHITELIST_GOING_TO_EXPIRE.add(dependency)
        lintableModule.WHITELIST_DEPENDENCIES.add(dependency)

        lintableModule.analizeDependency(dependency, projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryWhitelistedDependenciesLintTest is called report any Warning`() {
        lintableModule.reportWarnings(projects[LIBRARY_PROJECT]!!)
    }
}