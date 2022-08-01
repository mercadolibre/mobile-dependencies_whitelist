package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOW_LIST_URL

/**
 * This extension is in charge of verifying if the repository needs to execute lint and in which url to bring the allowlist.
 */
open class LintGradleExtension : ModuleOnOffExtension() {
    /** This variable contains the url from where to download the allowlist. */
    var dependencyAllowListUrl = ALLOW_LIST_URL
    /** This variable represents whether Release Lint is enabled. */
    var releaseDependenciesLintEnabled = true
    /** This variable represents whether lint dependencies is enabled. */
    var dependenciesLintEnabled = true
}
