package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project

class AppLintModule: LintableModule() {

    override fun getVariants(project: Project): List<BaseVariant> {
        val variants = arrayListOf<BaseVariant>()
        findExtension<AppExtension>(project)?.apply {
            applicationVariants.all { variants.add(this) }
        }
        return variants
    }

    override fun getLinter(): Lint {
        return ReleaseDependenciesLint()
    }
}