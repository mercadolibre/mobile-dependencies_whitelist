package com.mercadolibre.android.gradle.base.lint

/**
 * Created by saguilera on 7/25/17.
 */
class LintGradleExtension {

    // If the plugin should run or not
    boolean enabled = true

    // Whitelist url where it will fetch the json
    String dependencyWhitelistUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"

}
