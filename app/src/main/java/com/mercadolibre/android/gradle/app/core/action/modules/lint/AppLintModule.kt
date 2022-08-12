package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project

/**
 * This class takes care of the Lint of the App type modules.
 */
class AppLintModule : LintableModule() {

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(project: Project): Lint = ReleaseDependenciesLint()
}
