package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics

import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension

/**
 * This extension is responsible for capturing the exclusions that the repository wants to declare.
 */
open class JacocoConfigurationExtension : ModuleOnOffExtension() {
    /**
     * This is the list of files that jacoco will not review.
     */
    var excludeList = listOf<String>()
}
