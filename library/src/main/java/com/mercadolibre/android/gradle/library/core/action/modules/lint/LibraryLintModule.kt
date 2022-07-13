package com.mercadolibre.android.gradle.library.core.action.modules.lint

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project

class LibraryLintModule: LintableModule() {

    override fun getVariants(project: Project): List<BaseVariant> {
        val variants = arrayListOf<BaseVariant>()
        findExtension<LibraryExtension>(project)?.apply {
            libraryVariants.all { variants.add(this) }
        }
        return variants
    }

    override fun getLinter(): Lint {
        return LibraryAllowListDependenciesLint()
    }
}