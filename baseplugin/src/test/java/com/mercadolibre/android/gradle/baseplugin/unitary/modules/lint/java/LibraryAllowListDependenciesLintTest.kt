package com.mercadolibre.android.gradle.baseplugin.unitary.modules.lint.java

import com.google.gson.JsonObject
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP2
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP3
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_1
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_2
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(JUnit4::class)
class LibraryAllowListDependenciesLintTest : AbstractPluginManager() {

    private val lintableModule = LibraryAllowListDependenciesLint(listOf(ANY_NAME))

    private val UNDEFINED = ".*"

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        PluginConfigurer(LIBRARY_PLUGINS).configureProject(projects[LIBRARY_PROJECT]!!)

        projects[LIBRARY_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)

        mockkObject(JsonUtils)
        mockkObject(OutputUtils)
    }

    @org.junit.Test
    fun `When the module name is called return the correct value`() {
        assert(lintableModule.name() == "lintDependencies")
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
    fun `When the Lint is called analize any Dependency and a Pom is listed, do not report anything`() {
        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)

        // Available Version
        val availableVersion = Dependency(ANY_GROUP, UNDEFINED, UNDEFINED, null, null)

        // The dependecy exist in allow list but expire tomorrow
        lintableModule.allowListDependencies.addAll(listOf(dependency, availableVersion))

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Detect deprecated Dependencies
        lintableModule.lint(projects[LIBRARY_PROJECT]!!)

        // Do not report any warning
        verify(exactly = 0) { OutputUtils.logWarning(any<String>()) }
        verify(exactly = 0) { OutputUtils.logError(any<String>()) }
    }

    @org.junit.Test
    fun `When the Lint is called analize any Dependency listed and not report anything`() {
        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)

        // The dependecy exist in allow list
        lintableModule.allowListDependencies.add(dependency)

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Do not report anything
        verify(inverse = true) { OutputUtils.logError(any<String>()) }
        verify(inverse = true) { OutputUtils.logWarning(any<String>()) }
    }

    @org.junit.Test
    fun `When the Lint is called analize any Dependency listed with expire Available and not report anything`() {
        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, Long.MAX_VALUE, null)

        // The dependecy exist in allow list
        lintableModule.allowListDependencies.add(dependency)

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Do not report anything
        verify(inverse = true) { OutputUtils.logError(any<String>()) }
        verify(inverse = true) { OutputUtils.logWarning(any<String>()) }
    }

    @org.junit.Test
    fun `When the Lint is called analize any Dependency not listed and report it`() {
        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Report error of the the dependency not listed
        verify { OutputUtils.logError("The following dependencies are not allowed:") }
        verify { OutputUtils.logMessage("- anyGroup:anyName:1.0.0 (Invalid)") }
    }

    @org.junit.Test
    fun `When the Lint is called analize any Dependency listed but expired and report anything`() {
        // Get yesterday time in millis
        val yesterday = (System.currentTimeMillis() - 8.64e+7).toLong()
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd").format(Date(yesterday))

        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, yesterday, yesterdayDate)

        // The dependecy exist in allow list but expired
        lintableModule.allowListDependencies.add(dependency)

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Do not report any warning
        verify(exactly = 1) { OutputUtils.logError(any<String>()) }

        // Report the error of the expired dependency
        verify { OutputUtils.logError("The following dependencies are not allowed:") }
        verify { OutputUtils.logMessage("- anyGroup:anyName:1.0.0 (Expired)") }
    }

    @org.junit.Test
    fun `When the Lint is called analize any Dependency listed but expire tomorrow and only report a warning`() {
        // Get tomorrow time in millis
        val tomorrow = (System.currentTimeMillis() + 8.64e+7).toLong()
        val tomorrowDate = SimpleDateFormat("yyyy-MM-dd").format(Date(tomorrow))

        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, tomorrow, tomorrowDate)

        // The dependecy exist in allow list but expire tomorrow
        lintableModule.allowListDependencies.add(dependency)

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Detect deprecated Dependencies
        lintableModule.lint(projects[LIBRARY_PROJECT]!!)

        // Do not report any error
        verify(exactly = 0) { OutputUtils.logError(any<String>()) }

        // Report the warning of the deprecated dependency
        verify { OutputUtils.logWarning("The following dependencies has been marked as deprecated:") }
        verify { OutputUtils.logMessage("($tomorrowDate) - anyGroup:anyName:1.0.0 (Deprecated!) ") }
    }

    @org.junit.Test
    fun `When the Lint is called get Available version (Dependency), check others dependencies and report them`() {
        // Get yesterday time in millis
        val yesterday = (System.currentTimeMillis() - 8.64e+7).toLong()
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd").format(Date(yesterday))

        // Create the dependency
        val dependency = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, yesterday, yesterdayDate)

        // Available Version
        val availableVersion = Dependency(ANY_GROUP, ANY_NAME, VERSION_2, null, null)

        // The dependecy exist in allow list but expire tomorrow
        lintableModule.allowListDependencies.addAll(listOf(dependency, availableVersion))

        // Check if the dependency is in the allow list
        lintableModule.analyzeProjectDependency(dependency, projects[LIBRARY_PROJECT]!!)

        // Detect deprecated Dependencies
        lintableModule.lint(projects[LIBRARY_PROJECT]!!)

        // Do not report any warning
        verify(exactly = 0) { OutputUtils.logWarning(any<String>()) }

        // Report the error of the expired dependency with the available version
        verify { OutputUtils.logError("The following dependencies are not allowed:") }
        verify { OutputUtils.logMessage("- anyGroup:anyName:1.0.0 (Expired) Available version --> 2.0.0") }
    }

    @org.junit.Test
    fun `When the Lint is called analize multiple Dependencies listed but expired and report all of them`() {
        // Get yesterday time in millis
        val yesterday = (System.currentTimeMillis() - 8.64e+7).toLong()
        val yesterdayDate = SimpleDateFormat("yyyy-MM-dd").format(Date(yesterday))

        // Create the dependencies
        val dependency1 = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, yesterday, yesterdayDate)
        val dependency2 = Dependency(ANY_GROUP2, ANY_NAME, VERSION_1, yesterday, yesterdayDate)
        val dependency3 = Dependency(ANY_GROUP3, ANY_NAME, VERSION_1, yesterday, yesterdayDate)

        // The dependecies exist in allow list but expire tomorrow
        lintableModule.allowListDependencies.addAll(listOf(dependency1, dependency2, dependency3))

        // Check if the dependencies is in the allow list and not expired
        lintableModule.analyzeProjectDependency(dependency1, projects[LIBRARY_PROJECT]!!)
        lintableModule.analyzeProjectDependency(dependency2, projects[LIBRARY_PROJECT]!!)
        lintableModule.analyzeProjectDependency(dependency3, projects[LIBRARY_PROJECT]!!)

        // Detect deprecated Dependencies
        lintableModule.lint(projects[LIBRARY_PROJECT]!!)

        // Do not report any warning
        verify(exactly = 0) { OutputUtils.logWarning(any<String>()) }

        // Report the error of the expired dependency with the available version
        verify { OutputUtils.logError("The following dependencies are not allowed:") }
        verify { OutputUtils.logMessage("- anyGroup:anyName:1.0.0 (Expired)") }
        verify { OutputUtils.logMessage("- anyGroup2:anyName:1.0.0 (Expired)") }
        verify { OutputUtils.logMessage("- anyGroup3:anyName:1.0.0 (Expired)") }
    }
}
