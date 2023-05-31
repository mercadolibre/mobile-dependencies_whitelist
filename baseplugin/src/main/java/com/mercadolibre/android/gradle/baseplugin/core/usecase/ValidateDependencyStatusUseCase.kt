package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds

internal object ValidateDependencyStatusUseCase {

    fun validate(dependencyAnalysis: DependencyAnalysis): StatusBase {
        dependencyAnalysis.apply {
            if(expires.isNullOrEmpty() && availableVersion.isNullOrEmpty().not()) {
                return Status.available()
            }

            val hasNotMatchedAnyOnAllowList = notFound.isNotEmpty()
            if (hasNotMatchedAnyOnAllowList) {
                return Status.invalid(availableVersion)
            }

            expires?.let {
                return if (it.asMilliseconds() == Long.MAX_VALUE) {
                    Status.available()
                } else if (it.asMilliseconds() > System.currentTimeMillis()) {
                    Status.goingToExpire(availableVersion)
                } else {
                    Status.expired(availableVersion)
                }
            }

            if (!isAllowedAlpha) {
                Status.alphaDenied()
            }

            return Status.invalid(availableVersion)
        }
    }
}
