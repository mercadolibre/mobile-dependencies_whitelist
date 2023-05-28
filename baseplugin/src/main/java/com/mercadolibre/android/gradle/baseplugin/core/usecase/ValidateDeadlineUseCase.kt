package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds

internal object ValidateDeadlineUseCase {
    fun validate(allowListDependency: Dependency): Boolean {
        return allowListDependency.expires == null ||
            allowListDependency.expires.asMilliseconds() == Long.MAX_VALUE
    }
}
