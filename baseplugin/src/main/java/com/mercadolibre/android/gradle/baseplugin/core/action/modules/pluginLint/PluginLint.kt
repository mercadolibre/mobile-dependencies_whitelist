package com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint.plugins.Plugin
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

/**
 * The PluginLintModule class is in charge of reviewing all the dependencies of the project through the AllowList to
 * report if there is any deprecated in a Project.
 */
class PluginLint(private val projectType: String) : Lint() {

    // The task name
    private val TASK_NAME_PLUGIN_LINT = "pluginLint"

    // Path of the report files, warning and error
    private val PLUGIN_LINT_FILE_ERROR = "build/reports/PluginLint/lint.ld"
    private val PLUGIN_LINT_FILE_WARNING = "build/reports/PluginLint/lintWarning.ld"

    // The link to redirect the developer
    private val WIKI_LINK = "https://sites.google.com/mercadolibre.com/mobile/arquitectura/allowlist"

    // Key word to replace with content
    private val CONTENT_PLACEHOLDER = "<CONTENT>"

    private val DATE_PATTERN = "yyyy-MM-dd"

    // The title of the reports
    private val PLUGIN_LINT_TITLE = "The following plugins need to be reviewed in the module $CONTENT_PLACEHOLDER :"

    // The postdata of the reports
    private val PLUGIN_LINT_POSTDATA =
        "\nYour project has a plugin that matched with one listed in Meli Plugin." +
            "\nPlease follow the suggested link to know how to procced. $WIKI_LINK"

    /**
     * This list contains the dependencies in the allow list.
     */
    val allowListPlugins = arrayListOf(
        Plugin(
            "kotlin-android-extensions",
            isRequired = false,
            isBlocker = false,
            "We no longer recommend using this plugin, more info here:" +
                "https://android-developers.googleblog.com/2020/11/the-future-of-kotlin-android-extensions.html",
            "library|app",
            null
        ),
        Plugin(
            "com.monits.staticCodeAnalysis",
            isRequired = false,
            isBlocker = false,
            "We no longer recommend using this plugin, more info here:" +
                "https://sites.google.com/mercadolibre.com/mobile/arquitectura/listed-plugins/sca-plugin",
            "library|app",
            null
        )
    )

    /**
     * This method is responsible for providing a name to the linteo class.
     */
    override fun name(): String = TASK_NAME_PLUGIN_LINT

    private fun pluginsMatchWithStandars(plugin: Plugin, project: Project): Boolean {
        if (project.plugins.hasPlugin(plugin.id) && !plugin.isRequired) { // We want the plugin to be applied
            return true
        } else if (!project.plugins.hasPlugin(plugin.id) && plugin.isRequired) { // We do not want the plugin to be applied
            return true
        }
        return false // There is no match to report
    }

    private fun nowIsBlocker(plugin: Plugin): Boolean {
        return if (plugin.expires == null) { // The plugin has expired
            true
        } else {
            when {
                plugin.expires == Long.MAX_VALUE -> true // The plugin has expired
                System.currentTimeMillis() < plugin.expires -> false // The plugin not has expired
                else -> true // The plugin has expired
            }
        }
    }

    private fun makeCompleteTitle(project: Project): String {
        return PLUGIN_LINT_TITLE.replace(CONTENT_PLACEHOLDER, project.name) // Complete the title with the dynamic content
    }

    private fun formatDate(date: Long): String {
        return SimpleDateFormat(DATE_PATTERN).format(Date(date))
    }

    private fun logReport(project: Project, list: List<String>, isError: Boolean) {
        if (isError) {
            OutputUtils.logError(makeCompleteTitle(project))
        } else {
            OutputUtils.logWarning(makeCompleteTitle(project))
        }
        for (pluginWithError in list) {
            OutputUtils.logMessage(pluginWithError)
        }
    }

    private fun logReports(project: Project, pluginsToReportWithError: ArrayList<String>, pluginsToReportWithWarning: ArrayList<String>) {
        // Exist any error report
        if (pluginsToReportWithError.isNotEmpty()) {
            // Print errors
            logReport(project, pluginsToReportWithError, true)
            // Print wiki data
            OutputUtils.logMessage(PLUGIN_LINT_POSTDATA)
            // Print a separator
            OutputUtils.logMessage("")
        }

        // Exist any warining report
        if (pluginsToReportWithWarning.isNotEmpty()) {
            // Print warnings
            logReport(project, pluginsToReportWithWarning, false)
            // Print wiki data
            OutputUtils.logMessage(PLUGIN_LINT_POSTDATA)
        }
    }

    private fun saveReport(project: Project, plugin: Plugin, errorMessage: Boolean): String {
        var completeMessage = "${plugin.id} - ${plugin.customMessage}"

        if (plugin.expires != null) {
            completeMessage = "(${formatDate(plugin.expires)}) $completeMessage"
        }

        val messageWithTitle = "${makeCompleteTitle(project)}\n$completeMessage"

        OutputUtils.writeAReportMessage(messageWithTitle, project.file(if (errorMessage) PLUGIN_LINT_FILE_ERROR else PLUGIN_LINT_FILE_WARNING))

        return completeMessage
    }

    private fun cleanOutputs(project: Project) {
        for (OutputFile in listOf(PLUGIN_LINT_FILE_ERROR, PLUGIN_LINT_FILE_WARNING)) {
            project.file(OutputFile).apply {
                // Old error or warning file exist ?
                if (exists()) {
                    // Delete old error or warning file
                    delete()
                }
            }
        }
    }

    private fun completeFilesWithPostData(project: Project) {
        for (reportFile in listOf(PLUGIN_LINT_FILE_ERROR, PLUGIN_LINT_FILE_WARNING)) {
            project.file(reportFile).apply {
                // Already exist a new report file ?
                if (exists()) {
                    // Write postdata message to file report
                    OutputUtils.writeAReportMessage(PLUGIN_LINT_POSTDATA, this)
                }
            }
        }
    }

    private fun matchWithTheModuleType(plugin: Plugin): Boolean {
        return Pattern.compile(plugin.type, Pattern.CASE_INSENSITIVE).matcher(projectType).matches()
    }

    /**
     * This method is responsible for verifying that the plugins of the project are valid or
     * if they are about to expire, perform the warning.
     */
    override fun lint(project: Project): Boolean {
        var hasFailed = false

        cleanOutputs(project)

        val pluginsToReportWithError = arrayListOf<String>()
        val pluginsToReportWithWarning = arrayListOf<String>()

        findExtension<LintGradleExtension>(project)?.apply {
            if (pluginsLintEnabled) {
                for (plugin in allowListPlugins) {
                    if (pluginsMatchWithStandars(plugin, project) && matchWithTheModuleType(plugin)) {
                        val nowIsBlocker = plugin.isBlocker && nowIsBlocker(plugin)

                        val completeMessage = saveReport(project, plugin, nowIsBlocker)

                        if (nowIsBlocker) {
                            pluginsToReportWithError.add(completeMessage)
                        } else {
                            pluginsToReportWithWarning.add(completeMessage)
                        }

                        if (!hasFailed && nowIsBlocker) {
                            hasFailed = nowIsBlocker
                        }
                    }
                }
                logReports(project, pluginsToReportWithError, pluginsToReportWithWarning)
                completeFilesWithPostData(project)
            }
        }

        return hasFailed
    }
}
