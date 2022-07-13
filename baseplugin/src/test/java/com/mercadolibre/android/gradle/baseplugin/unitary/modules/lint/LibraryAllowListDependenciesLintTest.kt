package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint

import com.google.gson.JsonObject
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOW_LIST_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_BLOCKER
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_WARNING
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_REPORT_ERROR
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNING_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP2
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP3
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_1
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_2
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_3
import io.mockk.every
import io.mockk.mockk
import org.gradle.kotlin.dsl.apply
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(JUnit4::class)
class LibraryAllowListDependenciesLintTest : AbstractPluginManager() {

    private val lintableModule = LibraryAllowListDependenciesLint()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        findExtension<LintGradleExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
            this.enabled = true
            this.dependenciesLintEnabled = true
        }

        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)
        projects[LIBRARY_PROJECT]!!.apply(plugin = LIBRARY_PLUGIN)
    }

    @org.junit.Test
    fun `When one Status Base is created works`() {
        val status = StatusBase(shouldReport = false, isBlocker = true, name = ANY_NAME)
        Status().goignToExpire()
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
            dependencyAllowListUrl = ALLOW_LIST_URL
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
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, "")

        lintableModule.ALLOWLIST_GOING_TO_EXPIRE.add(dependency)

        val variant = mockVariant()
        lintableModule.lint(projects[LIBRARY_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is enabled and lint is called whit a error`() {
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, "")

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

        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)
        val dependency2 = Dependency(ANY_GROUP2, ANY_NAME, VERSION_1, null, null)
        val dependency3 = Dependency(ANY_GROUP3, null, VERSION_3, null, null)
        val dependency4 = Dependency(ANY_GROUP3, ANY_NAME, VERSION_3, null, null)

        val dependencyExpired = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, (System.currentTimeMillis() - 2.592e+9).toLong(), currentDate)
        val dependencyExpired2 = Dependency(ANY_GROUP2, ANY_NAME, "$VERSION_1|$VERSION_2", (System.currentTimeMillis() - 2.592e+9).toLong(), currentDate)
        val dependencyExpired3 = Dependency(ANY_GROUP3, null, VERSION_3, null, currentDate)
        val dependencyExpired4 = Dependency(ANY_GROUP3, ANY_NAME, null, null, currentDate)

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
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency Deprecated`() {
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, "")

        projects[LIBRARY_PROJECT]!!.file("./$LINT_LIBRARY_FILE_WARNING").mkdirs()

        lintableModule.ALLOWLIST_GOING_TO_EXPIRE.add(dependency)

        lintableModule.reportWarnings(projects[LIBRARY_PROJECT]!!)

        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(LINT_LIBRARY_FILE_WARNING)
        val lintOutPut = lintWarningFile.inputStream().bufferedReader().use { it.readText() }

        assert(lintOutPut.contains("WARNING: The following dependencies has been marked as deprecated:"))
        assert(lintOutPut.contains("(null) - $ANY_GROUP:$ANY_NAME:$VERSION_1 (Deprecated!)"))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency Invalid`() {
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, "")

        lintableModule.analyzeDependency(dependency, projects[LIBRARY_PROJECT]!!)

        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(LINT_LIBRARY_FILE_BLOCKER)
        val lintOutPut = lintWarningFile.inputStream().bufferedReader().use { it.readText() }

        assert(lintOutPut.contains("ERROR: The following dependencies are not allowed:"))
        assert(lintOutPut.contains("- $ANY_GROUP:$ANY_NAME:$VERSION_1 (Invalid)"))
    }

    @org.junit.Test
    fun `When the LibraryAllowListDependenciesLintTest is called analize any Dependency not Expired`() {
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)

        lintableModule.ALLOWLIST_DEPENDENCIES.add(dependency)

        lintableModule.analyzeDependency(dependency, projects[LIBRARY_PROJECT]!!)

        val lintErrorFile = projects[LIBRARY_PROJECT]!!.file(LINT_LIBRARY_FILE_BLOCKER)
        val lintWarningFile = projects[LIBRARY_PROJECT]!!.file(LINT_LIBRARY_FILE_WARNING)

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
        val expected = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)

        val item = JsonObject()
        item.addProperty(GROUP_CONSTANT, ANY_GROUP)
        item.addProperty(NAME_CONSTANT, ANY_NAME)
        item.addProperty(VERSION_CONSTANT, VERSION_1)
        item.addProperty(EXPIRES_CONSTANT, "null")

        val actual = lintableModule.jsonNodeToDependency(item)

        Assert.assertEquals(expected.expires, actual.expires)
        Assert.assertEquals(expected.name, actual.name)
        Assert.assertEquals(expected.group, actual.group)
        Assert.assertEquals(expected.version, actual.version)
    }

    @org.junit.Test
    fun `When parsing a JsonElement get proper value`() {
        val expected = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)
        val item = JsonObject()
        item.addProperty(GROUP_CONSTANT, ANY_GROUP)
        item.addProperty(NAME_CONSTANT, ANY_NAME)
        item.addProperty(VERSION_CONSTANT, VERSION_1)

        Assert.assertEquals(expected.name, lintableModule.getVariableFromJson(NAME_CONSTANT, item, ".*"))
        Assert.assertEquals(expected.group, lintableModule.getVariableFromJson(GROUP_CONSTANT, item, ""))
        Assert.assertEquals(expected.version, lintableModule.getVariableFromJson(VERSION_CONSTANT, item, ".*"))
    }

    @org.junit.Test
    fun `When parsing a non valid field in JsonElement get default value`() {
        val expected: String? = null

        val item = JsonObject()
        item.addProperty(ANY_GROUP, ANY_GROUP)
        val actual = lintableModule.getVariableFromJson("no_exist", item, null)

        Assert.assertEquals(expected, actual)
    }
}
