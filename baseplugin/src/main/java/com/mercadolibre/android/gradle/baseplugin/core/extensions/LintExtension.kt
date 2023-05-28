package com.mercadolibre.android.gradle.baseplugin.core.extensions

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import org.gradle.api.Project

internal fun Lint.setup(project: Project): LintGradleExtension {
    return findExtension<LintGradleExtension>(project) ?: LintGradleExtension()
}