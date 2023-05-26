package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

/**
 * This class is the representation of a dependency within a project.
 * @param group This variable represents the dependency group.
 * @param name This variable represents the dependency name.
 * @param version This variable represents the dependency version.
 * @param expires This variable represents the dependency exprires.
 * @param rawExpiresDate This variable represents the dependency date.
 */
data class Dependency(
    val group: String?,
    val name: String?,
    val version: String?,
    val expires: Long?,
    val rawExpiresDate: String?,
    val isAlpha: Boolean?
)
