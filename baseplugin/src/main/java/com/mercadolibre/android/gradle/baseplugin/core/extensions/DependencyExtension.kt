package com.mercadolibre.android.gradle.baseplugin.core.extensions

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import java.util.regex.Pattern
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

internal fun Dependency.fullName() = with(this) { "$group:$name:$version" }

internal fun Dependency.id() = with(this) { "$group:$name" }

internal fun Dependency.isLocal(project: Project): Boolean {
    return project.rootProject.allprojects.find {
        fullName().contains("${it.group}:${it.name}")
    } != null
}

internal fun Dependency.isSameVersion(dependency: Dependency): Boolean {
    val matchGroup = dependency.group == group
    val matchName =  dependency.name == UNDEFINED_VERSION_PATTERN || dependency.name == name || dependency.name == null
    return matchGroup && matchName
}

internal fun Dependency.matches(dependency: Dependency): Boolean {
    val pattern = Pattern.compile(dependency.fullName(), Pattern.CASE_INSENSITIVE)
    return pattern.matcher(fullName()).matches()
}

internal fun Dependency.parseAvailable() = version?.replace("|", " or ")
