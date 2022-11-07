package com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project

class PluginLintModule(private val moduleType: String) :
    LintableModule("lintPlugins", "Lints the project plugins to check they are in the allowed allowlist") {

    override fun getLinter(project: Project): Lint = PluginLint(moduleType)
}
