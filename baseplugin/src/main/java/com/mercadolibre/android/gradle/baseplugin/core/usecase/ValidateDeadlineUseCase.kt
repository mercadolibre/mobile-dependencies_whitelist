package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds

internal object ValidateDeadlineUseCase {
    fun validate(allowListDep: Dependency): Boolean {
        return allowListDep.expires == null
                || allowListDep.expires.asMilliseconds() == Long.MAX_VALUE
    }
}