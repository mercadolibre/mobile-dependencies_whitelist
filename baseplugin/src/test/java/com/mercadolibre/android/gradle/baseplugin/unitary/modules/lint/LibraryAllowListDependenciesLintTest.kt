package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.google.gson.JsonObject
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_REPORT_ERROR
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNING_FILENAME
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.BaseLibraryPlugin
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(JUnit4::class)
class LibraryAllowListDependenciesLintTest : AbstractPluginManager() {

    private val libraryConfigurer = BaseLibraryPlugin()
    private val lintableModule = LibraryAllowListDependenciesLint()

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
    fun `When one Status Base is created works`() {
        val status = StatusBase(shouldReport = false, isBlocker = true, name = ANY_NAME)
        Status().goign_to_expire()
        try {
            Assert.fail(status.message(ANY_NAME))
        } catch (e: IllegalAccessException) {
            assert(e.message == LINT_REPORT_ERROR)
        }
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is disable and lint is called`() {

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            enabled = false
            dependenciesLintEnabled = false
            dependencyAllowListUrl =
                "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"
        }

        val variant = mockVariant()

        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is enabled and lint is called`() {
        val variant = mockVariant()
        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is enabled and lint is called whit a expire depedency`() {
        val dependency = Dependency("group", "name", "version", null, "")

        lintableModule.ALLOWLIST_GOING_TO_EXPIRE.add(dependency)

        val variant = mockVariant()
        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is enabled and lint is called whit a error`() {
        val dependency = Dependency("group", "name", "version", null, "")

        lintableModule.ALLOWLIST_GOING_TO_EXPIRE.add(dependency)

        val variant = mockVariant()
        lintableModule.hasFailed = true
        lintableModule.name()

        val dependencyForApi1 = mockk<org.gradle.api.artifacts.Dependency>() {
            every { name } returns ANY_NAME
            every { group } returns "group"
            every { version } returns null
        }
        val dependencyForApi2 = mockk<org.gradle.api.artifacts.Dependency>() {
            every { name } returns ANY_NAME
            every { group } returns "group"
            every { version } returns "1.0.0"
        }

        projects[LIBRARY_PROJECT]!!.configurations.findByName(API_CONSTANT)?.dependencies?.addAll(listOf(dependencyForApi1, dependencyForApi2))

        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependencies Expired`() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date((System.currentTimeMillis() - 2.592e+9).toLong()))

        val dependency = Dependency("group", "name", "1.0.0", null, null)
        val dependency2 = Dependency("group2", "name", "1.0.0", null, null)
        val dependency3 = Dependency("group3", null, "3.0.0", null, null)
        val dependency4 = Dependency("group3", "name", "3.0.0", null, null)

        val dependencyExpired = Dependency("group", "name", "1.0.0", (System.currentTimeMillis() - 2.592e+9).toLong(), currentDate)
        val dependencyExpired2 = Dependency("group2", "name", "1.0.0|2.0.0", (System.currentTimeMillis() - 2.592e+9).toLong(), currentDate)
        val dependencyExpired3 = Dependency("group3", null, "3.0.0", null, currentDate)
        val dependencyExpired4 = Dependency("group3", "name", null, null, currentDate)

        lintableModule.ALLOWLIST_DEPENDENCIES.addAll(listOf(dependencyExpired, dependencyExpired2, dependencyExpired3, dependencyExpired4))

        lintableModule.analyzeDependency(dependency, projects[LIBRARY_PROJECT]!!)
        lintableModule.analyzeDependency(dependency2, projects[LIBRARY_PROJECT]!!)
        lintableModule.analyzeDependency(dependency3, projects[LIBRARY_PROJECT]!!)
        lintableModule.analyzeDependency(dependency4, projects[LIBRARY_PROJECT]!!)

        val lintErrorFile = projects[LIBRARY_PROJECT]!!.file(
            "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_FILENAME"
        )
        val lintOutPut = lintErrorFile.inputStream().bufferedReader().use { it.readText() }

        assert(lintOutPut.contains("ERROR: The following dependencies are not allowed:"))
        assert(lintOutPut.contains("- group:name:1.0.0 (Expired)"))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency Deprecated`() {
        val dependency = Dependency("group", "name", "version", null, "")

        lintableModule.ALLOWLIST_GOING_TO_EXPIRE.add(dependency)

        lintableModule.reportWarnings(projects[LIBRARY_PROJECT]!!)

        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(
            "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_WARNING_FILENAME"
        )
        val lintOutPut = lintWarningFile.inputStream().bufferedReader().use { it.readText() }

        assert(lintOutPut.contains("WARNING: The following dependencies has been marked as deprecated:"))
        assert(lintOutPut.contains("(null) - group:name:version (Deprecated!)"))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency Invalid`() {
        val dependency = Dependency("group", "name", "version", null, "")

        lintableModule.analyzeDependency(dependency, projects[LIBRARY_PROJECT]!!)

        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(
            "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_FILENAME"
        )
        val lintOutPut = lintWarningFile.inputStream().bufferedReader().use { it.readText() }

        assert(lintOutPut.contains("ERROR: The following dependencies are not allowed:"))
        assert(lintOutPut.contains("- group:name:version (Invalid)"))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency not Expired`() {
        val dependency = Dependency("group", "name", "1.0.0", null, null)

        lintableModule.ALLOWLIST_DEPENDENCIES.add(dependency)

        lintableModule.analyzeDependency(dependency, projects[LIBRARY_PROJECT]!!)

        val lintErrorFile = projects[LIBRARY_PROJECT]!!.file(
            "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_FILENAME"
        )
        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(
            "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_WARNING_FILENAME"
        )

        assert(!lintErrorFile.exists())
        assert(!lintWarningFile.exists())
    }

    @org.junit.Test
    fun `When casting a JsonElement without expires should get null date`() {
        val item = JsonObject()
        val actual = lintableModule.castJsonElementToDate(item)

        Assert.assertEquals("msg", null, actual)
    }

    @org.junit.Test
    fun `When casting a JsonElement to Dependency get a valid dependency`() {
        val expected = Dependency("group", "name", "1.0.0", null, null)

        val item = JsonObject()
        item.addProperty("group", "group")
        item.addProperty("name", "name")
        item.addProperty("version", "1.0.0")
        item.addProperty(EXPIRES_CONSTANT, "null")

        val actual = lintableModule.jsonNodeToDependency(item)

        Assert.assertEquals(expected.expires, actual.expires)
        Assert.assertEquals(expected.name, actual.name)
        Assert.assertEquals(expected.group, actual.group)
        Assert.assertEquals(expected.version, actual.version)
    }

    @org.junit.Test
    fun `When parsing a JsonElement get proper value`() {
        val expected = Dependency("group", "name", "1.0.0", null, null)
        val item = JsonObject()
        item.addProperty("group", "group")
        item.addProperty("name", "name")
        item.addProperty("version", "1.0.0")

        Assert.assertEquals(expected.name, lintableModule.getVariableFromJson("name", item, ".*"))
        Assert.assertEquals(expected.group, lintableModule.getVariableFromJson("group", item, ""))
        Assert.assertEquals(expected.version, lintableModule.getVariableFromJson("version", item, ".*"))
    }

    @org.junit.Test
    fun `When parsing a non valid field in JsonElement get default value`() {
        val expected: String? = null

        val item = JsonObject()
        item.addProperty("group", "group")
        val actual = lintableModule.getVariableFromJson("no_exist", item, null)

        Assert.assertEquals(expected, actual)
    }
}
