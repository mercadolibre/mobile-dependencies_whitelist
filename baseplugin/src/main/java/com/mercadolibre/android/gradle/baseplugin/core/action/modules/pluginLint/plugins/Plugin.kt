package com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginLint.plugins

/**
 * This class is the representation of a dependency within a project.
 * @param group This variable represents the dependency group.
 * @param name This variable represents the dependency name.
 * @param version This variable represents the dependency version.
 * @param expires This variable represents the dependency exprires.
 * @param rawExpiresDate This variable represents the dependency date.
 */
data class Plugin(
    val id: String,
    val isRequired: Boolean,
    val isBlocker: Boolean,
    val customMessage: String?,
    val type: String,
    val expires: Long?,
)
