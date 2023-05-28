package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import java.util.regex.Pattern

internal object MatchDependenciesUseCase {

    internal fun match(
        projectDependency: Dependency,
        allowedDependency: Dependency
    ): Boolean {
        val pattern = Pattern.compile(allowedDependency.fullName(), Pattern.CASE_INSENSITIVE)
        return pattern.matcher(projectDependency.fullName()).matches()
    }
}