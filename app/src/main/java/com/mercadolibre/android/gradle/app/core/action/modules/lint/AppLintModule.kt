package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project

/**
 * This class takes care of the Lint of the App type modules.
 */
class AppLintModule : LintableModule() {

    /**
     * This method is responsible for collecting the variants of the module.
     */
    override fun getVariants(project: Project): List<BaseVariant> {
        val variants = arrayListOf<BaseVariant>()
        findExtension<AppExtension>(project)?.apply {
            applicationVariants.all { variants.add(this) }
        }
        return variants
    }

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(): Lint = ReleaseDependenciesLint()
}
