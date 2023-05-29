package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

/**
 * This class contains the information of a dependency on the allowlist.
 * @param allowListDependency The data of the dependency, group, name and version.
 * @param availableVersion The version avaiable of this dependency.
 */
internal class DependencyAnalysis(
    var allowListDependency: Dependency,
    var projectDependency: Dependency,
    var availableVersion: String? = null,
    var isAllowedAlpha: Boolean = false
)
