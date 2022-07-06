package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

open class LintGradleExtension {
    var dependencyAllowListUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"
    var releaseDependenciesLintEnabled = true
    var dependenciesLintEnabled = true
    var enabled = true
}
