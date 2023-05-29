package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

internal object MatchDependenciesUseCase {

    internal fun match(
        projectDependency: Dependency,
        allowedDependency: Dependency
    ): Boolean {
        val pattern = Pattern.compile(allowedDependency.fullName(), CASE_INSENSITIVE)
        return pattern.matcher(projectDependency.fullName()).matches()
    }
}
