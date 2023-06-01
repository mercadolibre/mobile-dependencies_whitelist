package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds

internal object ValidateDependencyStatusUseCase {

    fun validate(dependencyAnalysis: DependencyAnalysis): StatusBase {
        dependencyAnalysis.apply {
            val hasNotMatchedAnyOnAllowList = notFound.isNotEmpty()
            if (hasNotMatchedAnyOnAllowList) {
                return Status.invalid(availableVersion)
            }
            if (!isAllowedAlpha) {
                Status.alphaDenied()
            }
            allowListDependency?.apply {
                if (expires.isNullOrEmpty()) {
                    return Status.available()
                }
                return if (expires.asMilliseconds() == Long.MAX_VALUE) {
                    Status.available()
                } else if (expires.asMilliseconds() > System.currentTimeMillis()) {
                    Status.goingToExpire(availableVersion)
                } else {
                    Status.expired(availableVersion)
                }
            }
            return Status.invalid(availableVersion)
        }
    }
}
