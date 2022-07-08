package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOW_LIST_URL

/**
 * This extension is in charge of verifying if the repository needs to execute lint and in which url to bring the allowlist.
 */
open class LintGradleExtension {
    var dependencyAllowListUrl = ALLOW_LIST_URL
    var releaseDependenciesLintEnabled = true
    var dependenciesLintEnabled = true
    var enabled = true
}
