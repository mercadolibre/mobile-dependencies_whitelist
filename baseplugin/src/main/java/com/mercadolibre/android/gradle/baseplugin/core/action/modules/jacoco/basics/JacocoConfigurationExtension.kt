package com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics

/**
 * This extension is responsible for capturing the exclusions that the repository wants to declare.
 */
open class JacocoConfigurationExtension {
    var excludeList = listOf<String>()
}
