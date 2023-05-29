package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds
import com.mercadolibre.android.gradle.baseplugin.core.extensions.isSameVersion

internal object ValidateDeadlineUseCase {
    fun validate(
        projectDependency: Dependency,
        allowListDependency: Dependency
    ): Boolean {
        val nonSettledExpire = allowListDependency.expires == null
        val settledExpireTooFarAway = allowListDependency.expires?.asMilliseconds() == Long.MAX_VALUE
        (projectDependency.isSameVersion(allowListDependency)
                && (nonSettledExpire || settledExpireTooFarAway)).let { isUpToDate ->
                    return isUpToDate
        }
    }
}
