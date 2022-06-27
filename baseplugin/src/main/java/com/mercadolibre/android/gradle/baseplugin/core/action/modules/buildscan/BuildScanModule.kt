package com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan

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

class BuildScanModule: Module, SettingsModule, ExtensionGetter() {

    fun configure(obj: PluginAware, projectName: String) {
        if (obj is Project || obj is Settings){
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
            text("git config --get remote.origin.url").text.trim()

            val commitId = text("git rev-parse --verify HEAD").text.trim()
            value("Git Commit ID", commitId)
            val branchName = text("git rev-parse --abbrev-ref HEAD").text.trim()
            value("Git branch", branchName)
            value("user_name", text("git config user.name").text.trim())
            value("user_email", text("git config user.email").text.trim().trim())
            value("remote_url", text("git config --get remote.origin.url").text.trim().trim())
        }
    }

    override fun configure(settings: Settings) {
        configure(settings, settings.rootProject.name)
    }

    override fun configure(project: Project) {
        configure(project, project.name)
    }

}