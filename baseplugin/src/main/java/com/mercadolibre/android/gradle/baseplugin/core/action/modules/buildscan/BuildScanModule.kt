package com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan

import com.android.tools.build.bundletool.model.utils.files.BufferedIo.inputStream
import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.scan.plugin.BuildScanExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.execution.text
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

/**
 * The BuildScan module is responsible for providing the functionality of publishing the build to Gradle Enterprise.
 */
class BuildScanModule : Module, SettingsModule, ExtensionGetter() {

    fun configure(obj: PluginAware, projectName: String) {
        if (obj is Project || obj is Settings) {
            if (obj is Settings) {
                obj.apply(plugin = GRADLE_ENTERPRISE)
            }

            findExtension<GradleEnterpriseExtension>(obj as ExtensionAware)?.apply {
                configBuildScanExtension(buildScan, projectName)
            }
        }
    }

    fun configBuildScanExtension(gradleExtension: BuildScanExtension, projectName: String) {
        with(gradleExtension) {
            publishAlways()

            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"

            server = "https://gradle.adminml.com/"
            tag(projectName)
            isUploadInBackground = !System.getenv().containsKey("CI")

            if (System.getenv().containsKey("CI")) {
                tag("CI")
            } else {
                tag("Local")
            }

            background {
                configBackground(this)
            }
        }
    }

    fun configBackground(buildScanExtension: BuildScanExtension) {
        with(buildScanExtension) {
            value("Git Commit ID", getCommandText("git rev-parse --verify HEAD"))
            value("Git branch", getCommandText("git rev-parse --abbrev-ref HEAD"))
            value("user_name", getCommandText("git config user.name"))
            value("user_email", getCommandText("git config user.email"))
            value("remote_url", getCommandText("git config --get remote.origin.url"))
        }
    }

    private fun getCommandText(command: String): String {
        return getText(executeCommand(command))
    }

    private fun executeCommand(command: String): InputStream {
        return Runtime.getRuntime().exec(command).inputStream
    }

    private fun getText(inputStreamReader: InputStream): String {
        return BufferedReader(InputStreamReader(inputStreamReader, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"))
    }

    override fun configure(settings: Settings) {
        configure(settings, settings.rootProject.name)
    }

    override fun configure(project: Project) {
        configure(project, project.name)
    }
}
