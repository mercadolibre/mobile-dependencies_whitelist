package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName

internal object GetDependencyStatusUseCase {

    fun get(lint: LintGradleExtension, dependencyAnalysis: DependencyAnalysis): StatusBase {
        dependencyAnalysis.apply {
            if (dependency.expires == null) {
                return Status.available()
            } else if (lint.alphaDependenciesEnabled && !isAllowedAlpha) {
                return Status.alphaDenied()
            }

            dependency.expires?.let { date ->
                if (System.currentTimeMillis() < date.asMilliseconds()) {
                    return Status.goingToExpire(availableVersion)
                } else {
                    return Status.expired(availableVersion)
                }
            }

            return Status.invalid()
        }
    }
}
