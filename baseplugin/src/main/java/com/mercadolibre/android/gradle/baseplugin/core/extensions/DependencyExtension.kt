package com.mercadolibre.android.gradle.baseplugin.core.extensions

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency as VersionCatalogDependency

internal const val UNDEFINED_VERSION_PATTERN = ".*"

/**
 * This method is responsible for obtaining the nodes of the dependencies and storing them through the Data Class Dependency.
 */
internal fun Dependency.parseAllowlistDefaults() = copy(
    group = group?.replace("\\", "") ?: "",
    name = name?.replace("\\", "") ?: UNDEFINED_VERSION_PATTERN,
    version = version?.replace("\\", "") ?: UNDEFINED_VERSION_PATTERN,
)

internal fun VersionCatalogDependency.parseProjectDefaults() = Dependency(group, name, version, "", false)

internal fun Dependency.fullName() = with(this) {
    "$group:$name:$version"
}

internal fun Dependency.isLocal(project: Project): Boolean {
    return project.rootProject.allprojects.find {
        fullName().contains("${it.group}:${it.name}")
    } != null
}

internal fun Dependency.isSameVersion(dependency: Dependency): Boolean {
    return group == dependency.group &&
        (
            name == UNDEFINED_VERSION_PATTERN ||
                (name == dependency.name || name == null)
            )
}

internal fun Dependency.parseAvailable() = version?.replace("|", " or ")
