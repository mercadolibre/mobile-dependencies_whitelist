package com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin
import com.gradle.scan.plugin.BuildScanExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.CI_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMMAND_BRANCH
import com.mercadolibre.android.gradle.baseplugin.core.components.COMMAND_COMMIT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMMAND_EMAIL
import com.mercadolibre.android.gradle.baseplugin.core.components.COMMAND_REMOTE_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.COMMAND_USER_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.GIT_BRANCH
import com.mercadolibre.android.gradle.baseplugin.core.components.GIT_COMMIT
import com.mercadolibre.android.gradle.baseplugin.core.components.GIT_EMAIL
import com.mercadolibre.android.gradle.baseplugin.core.components.GIT_REMOTE_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.GIT_USER_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVER_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVICES_AGREE
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVICES_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.LOCAL_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule
import org.gradle.api.initialization.Settings
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

/**
 * The BuildScan module is responsible for providing the functionality of publishing the build to Gradle Enterprise.
 */
class BuildScanModule : SettingsModule, ExtensionGetter() {

    /**
     * This method is responsible for applying the Gradle Enterprise plugin and requesting that its extension be configured.
     */

    override fun configure(settings: Settings) {
        settings.plugins.apply(GradleEnterprisePlugin::class.java)

        findExtension<GradleEnterpriseExtension>(settings)?.apply {
            configBuildScanExtension(buildScan, settings.rootProject.name, System.getenv().containsKey(CI_CONSTANT))
        }
    }

    /**
     * This method is responsible for configuring the Gradle Enterprise extension to publish the Builds.
     */
    fun configBuildScanExtension(gradleExtension: BuildScanExtension, projectName: String, isBuildFromCI: Boolean) {
        with(gradleExtension) {
            publishAlways()

            termsOfServiceUrl = GRADLE_ENTERPRISE_SERVICES_URL
            termsOfServiceAgree = GRADLE_ENTERPRISE_SERVICES_AGREE

            server = GRADLE_ENTERPRISE_SERVER_URL
            tag(projectName)
            isUploadInBackground = !System.getenv().containsKey(CI_CONSTANT)

            if (isBuildFromCI) {
                tag(CI_CONSTANT)
            } else {
                tag(LOCAL_CONSTANT)
            }

            background {
                configBackground(this)
            }
        }
    }

    /**
     * This method is in charge of setting the context variables where the build is executed in order to be seen in Gradle Enterprise.
     */
    fun configBackground(buildScanExtension: BuildScanExtension) {
        with(buildScanExtension) {
            value(GIT_COMMIT, getCommandText(COMMAND_COMMIT))
            value(GIT_BRANCH, getCommandText(COMMAND_BRANCH))
            value(GIT_USER_NAME, getCommandText(COMMAND_USER_NAME))
            value(GIT_EMAIL, getCommandText(COMMAND_EMAIL))
            value(GIT_REMOTE_URL, getCommandText(COMMAND_REMOTE_URL))
        }
    }

    private fun getCommandText(command: String): String = getText(executeCommand(command))

    private fun executeCommand(command: String): InputStream = Runtime.getRuntime().exec(command).inputStream

    private fun getText(inputStreamReader: InputStream): String =
        BufferedReader(InputStreamReader(inputStreamReader, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"))
}
