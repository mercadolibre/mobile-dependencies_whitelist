package com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies

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
