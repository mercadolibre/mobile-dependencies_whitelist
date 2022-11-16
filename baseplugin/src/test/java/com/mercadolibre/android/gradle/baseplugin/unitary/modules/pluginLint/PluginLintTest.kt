package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginLint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint.PluginLint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint.plugins.Plugin
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.JsonUtils
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import java.text.SimpleDateFormat
import java.util.Date
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginLintTest : AbstractPluginManager() {

    private val pluginLintModule = PluginLint("library")
    private val pluginLintBaseModule = PluginLint("anyModule")

    lateinit var mockedSubProject: Project

    private val PLUGIN_LINT_FILE_ERROR = "build/reports/PluginLint/lint.ld"
    private val PLUGIN_LINT_FILE_WARNING = "build/reports/PluginLint/lintWarning.ld"

    private val PLUGIN_LINT_TITLE = "The following plugins need to be reviewed in the module p1 :"

    private val PLUGIN_REQUIRED = "plugin-required"
    private val PLUGIN_RECOMMENDED = "plugin-recommended"
    private val PLUGIN_BANED = "plugin-baned"
    private val PLUGIN_DEPRECATED = "plugin-deprecated"

    private val PLUGIN_TYPE = "app|library"

    @org.junit.Before
    fun setUp() {
        mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
        mockedSubProject = mockedRoot.subProjects[LIBRARY_PROJECT]!!.project

        pluginLintModule.allowListPlugins.clear()
        pluginLintBaseModule.allowListPlugins.clear()

        mockkObject(JsonUtils)
        mockkObject(OutputUtils)
        mockkObject(TimeStampManager)

        pluginLintModule.allowListPlugins.addAll(
            listOf(
                Plugin(PLUGIN_REQUIRED, isRequired = true, isBlocker = true, customMessage = PLUGIN_REQUIRED, type = PLUGIN_TYPE, null),
                Plugin(PLUGIN_RECOMMENDED, isRequired = true, isBlocker = false, customMessage = PLUGIN_RECOMMENDED, type = PLUGIN_TYPE, null),
                Plugin(PLUGIN_BANED, isRequired = false, isBlocker = true, customMessage = PLUGIN_BANED, type = PLUGIN_TYPE, null),
                Plugin(PLUGIN_DEPRECATED, isRequired = false, isBlocker = false, customMessage = PLUGIN_DEPRECATED, type = PLUGIN_TYPE, null),
            )
        )

        every { mockedSubProject.extensions.findByType(LintGradleExtension::class.java) } returns mockk(relaxed = true) {
            every { pluginsLintEnabled } returns true
        }

        every { mockedSubProject.file(PLUGIN_LINT_FILE_ERROR) } returns mockk(relaxed = true)
        every { mockedSubProject.file(PLUGIN_LINT_FILE_WARNING) } returns mockk(relaxed = true)
        every { mockedSubProject.file("build/reports/PluginLint/cache") } returns mockk(relaxed = true)

        every { OutputUtils.writeAReportMessage(any(), any()) } returns mockk(relaxed = true)
    }

    @org.junit.After
    fun after() {
        unmockkAll()
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and check the expires, know if it is already expired or not`() {
        val tomorrow = (System.currentTimeMillis() + 8.64e+7).toLong()
        val yesterday = (System.currentTimeMillis() - 8.64e+7).toLong()

        val pluginBanedYesterday = "plugin-baned-yesterday"
        val pluginBanedTomorrow = "plugin-baned-tomorrow"

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns true

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns false

        every { mockedSubProject.plugins.hasPlugin(pluginBanedTomorrow) } returns true
        every { mockedSubProject.plugins.hasPlugin(pluginBanedYesterday) } returns true

        pluginLintModule.allowListPlugins.addAll(
            listOf(
                Plugin(pluginBanedTomorrow, isRequired = false, isBlocker = true, customMessage = pluginBanedTomorrow, type = PLUGIN_TYPE, tomorrow),
                Plugin(pluginBanedYesterday, isRequired = false, isBlocker = true, customMessage = pluginBanedYesterday, type = PLUGIN_TYPE, yesterday)
            )
        )

        pluginLintModule.lint(mockedSubProject)

        val pattern = SimpleDateFormat("YYYY-MM-dd")
        val yesterdayDate = Date(yesterday)
        val tomorrowDate = Date(tomorrow)

        // Report the deprecated plugin
        verify { OutputUtils.logError(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("(${pattern.format(yesterdayDate)}) $pluginBanedYesterday - $pluginBanedYesterday") }

        // Report the allowed plugin (Tomorrow deprecated)
        verify { OutputUtils.logWarning(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("(${pattern.format(tomorrowDate)}) $pluginBanedTomorrow - $pluginBanedTomorrow") }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project has nothing to report, nothing is reported`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns true

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns false

        val lint = pluginLintModule.lint(mockedSubProject)

        assert(!lint)
        verify(inverse = true) { OutputUtils.logError(any<String>()) }
        verify(inverse = true) { OutputUtils.logWarning(any<String>()) }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project not match with any plugin allowed or blocked, nothing is reported`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns false

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns true

        val lintWithoutMachedListedPlugin = pluginLintBaseModule.lint(mockedSubProject)
        val lintWithMatchedListedPlugin = pluginLintModule.lint(mockedSubProject)

        // Dont fail beacouse the module type is not equals
        assert(!lintWithoutMachedListedPlugin)

        // Fail beacouse is the same module type
        assert(lintWithMatchedListedPlugin)

        verify { OutputUtils.logError(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_REQUIRED - $PLUGIN_REQUIRED") }
        verify { OutputUtils.logMessage("$PLUGIN_BANED - $PLUGIN_BANED") }

        verify { OutputUtils.logWarning(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_RECOMMENDED - $PLUGIN_RECOMMENDED") }
        verify { OutputUtils.logMessage("$PLUGIN_DEPRECATED - $PLUGIN_DEPRECATED") }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project has a blocker plugin to report, is reported`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns true

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns true

        val lint = pluginLintModule.lint(mockedSubProject)

        assert(lint)
        verify { OutputUtils.logError(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_BANED - $PLUGIN_BANED") }
        verify(inverse = true) { OutputUtils.logWarning(any<String>()) }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project has a required plugin to report, is reported`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns true

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns false

        val lint = pluginLintModule.lint(mockedSubProject)

        assert(lint)
        verify { OutputUtils.logError(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_REQUIRED - $PLUGIN_REQUIRED") }
        verify(inverse = true) { OutputUtils.logWarning(any<String>()) }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project has multiple reports, print all of them`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns false
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns false

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns true

        val lint = pluginLintModule.lint(mockedSubProject)

        assert(lint)
        verify { OutputUtils.logError(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_REQUIRED - $PLUGIN_REQUIRED") }
        verify { OutputUtils.logMessage("$PLUGIN_BANED - $PLUGIN_BANED") }

        verify { OutputUtils.logWarning(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_RECOMMENDED - $PLUGIN_RECOMMENDED") }
        verify { OutputUtils.logMessage("$PLUGIN_DEPRECATED - $PLUGIN_DEPRECATED") }
    }

    @org.junit.Test
    fun `When the Plugin Lint is executed and the project has warning, is reported`() {
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_REQUIRED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_RECOMMENDED) } returns false

        every { mockedSubProject.plugins.hasPlugin(PLUGIN_DEPRECATED) } returns true
        every { mockedSubProject.plugins.hasPlugin(PLUGIN_BANED) } returns false

        val lint = pluginLintModule.lint(mockedSubProject)

        assert(!lint)
        verify(inverse = true) { OutputUtils.logError(any<String>()) }
        verify { OutputUtils.logWarning(PLUGIN_LINT_TITLE) }
        verify { OutputUtils.logMessage("$PLUGIN_RECOMMENDED - $PLUGIN_RECOMMENDED") }
        verify { OutputUtils.logMessage("$PLUGIN_DEPRECATED - $PLUGIN_DEPRECATED") }
    }
}
