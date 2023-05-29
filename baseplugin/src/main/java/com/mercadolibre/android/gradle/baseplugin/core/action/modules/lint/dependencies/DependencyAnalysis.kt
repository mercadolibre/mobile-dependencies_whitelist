package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

/**
 * This class contains the information of a dependency on the allowlist.
 * @param availableVersion The version avaiable of this dependency.
 */
internal data class DependencyAnalysis(
    val projectDependency: Dependency? = null,
    val availableVersion: String? = null,
    val isAllowedAlpha: Boolean = false,
    val expires: String? = null,
    val status: StatusBase? = null
)
