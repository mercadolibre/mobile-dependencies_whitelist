package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.AlphaAllowedProjects
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import org.gradle.api.Project

internal object ValidateAlphaUseCase {

    fun validate(dependency: Dependency, project: Project): Boolean {
        var allowed = false
        if (dependency.isAlpha) {
            AlphaAllowedProjects.groups.forEach { alphaAllowedGroup ->
                if (alphaAllowedGroup == project.group) {
                    allowed = true
                }
            }
        }
        return allowed
    }
}