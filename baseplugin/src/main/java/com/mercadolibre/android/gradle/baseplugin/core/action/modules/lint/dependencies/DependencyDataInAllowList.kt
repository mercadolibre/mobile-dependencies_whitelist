package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

/**
 * This class contains the information of a dependency on the allowlist.
 * @param allowListDep The data of the dependency, group, name and version.
 * @param availableVersion The version avaiable of this dependency.
 */
data class DependencyDataInAllowList(var allowListDep: Dependency?, var availableVersion: String?)
