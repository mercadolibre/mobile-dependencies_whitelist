package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.extensions.asMilliseconds

internal object GetDependencyStatusUseCase {

    fun get(lint: LintGradleExtension, dependencyAnalysis: DependencyAnalysis?): StatusBase {
        dependencyAnalysis?.apply {
            dependency?.let {
                return if (it.expires == null) {
                    Status.available()
                } else if (lint.alphaDependenciesEnabled && !isAllowedAlpha) {
                    Status.alphaDenied()
                } else {
                    when {
                        it.expires.asMilliseconds() == Long.MAX_VALUE -> {
                            Status.available()
                        }

                        System.currentTimeMillis() < it.expires.asMilliseconds() -> {
                            Status.goingToExpire(availableVersion)
                        }

                        else -> {
                            Status.expired(availableVersion)
                        }
                    }
                }
            }
        }
        return Status.invalid(dependencyAnalysis?.availableVersion)
    }
}