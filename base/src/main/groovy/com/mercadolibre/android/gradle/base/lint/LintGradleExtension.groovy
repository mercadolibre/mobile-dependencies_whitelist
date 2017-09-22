package com.mercadolibre.android.gradle.base.lint

/**
 * Created by saguilera on 7/25/17.
 */
class LintGradleExtension {

    /**
     * If the plugin should run or not
     */
    boolean enabled = true

    /**
     * If the dependenciesLint should be run or not
     */
    boolean dependenciesLintEnabled = true

    /**
     * If the release dependencies lint should be ran in the Application/Library or not.
     * This lint will check that only release dependencies are being compiled in the application
     * This lint is only ran when targetting production branches in your CI environment
     */
    boolean releaseDependenciesLintEnabled = true

    /**
     * Whitelist url where it will fetch the json
     */
    String dependencyWhitelistUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"

}
