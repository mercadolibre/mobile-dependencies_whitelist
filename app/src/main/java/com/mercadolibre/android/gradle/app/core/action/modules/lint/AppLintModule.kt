package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import org.gradle.api.Project

/**
 * This class takes care of the Lint of the App type modules.
 */
class AppLintModule : LintableModule(LINTABLE_TASK, LINTABLE_DESCRIPTION) {

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(project: Project): Lint = ReleaseDependenciesLint()
}
