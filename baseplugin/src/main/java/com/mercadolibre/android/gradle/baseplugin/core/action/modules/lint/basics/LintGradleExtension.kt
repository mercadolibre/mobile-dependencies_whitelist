package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

/**
 * This extension is in charge of verifying if the repository needs to execute lint and in which url to bring the allowlist.
 */
open class LintGradleExtension {
    var dependencyAllowListUrl =
        "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"
    var releaseDependenciesLintEnabled = true
    var dependenciesLintEnabled = true
    var enabled = true
}
