package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

/**
 * This class is the representation of a dependency within a project.
 */
data class Dependency(
    val group: String?,
    val name: String?,
    val version: String?,
    val expires: Long?,
    val rawExpiresDate: String?,
)
