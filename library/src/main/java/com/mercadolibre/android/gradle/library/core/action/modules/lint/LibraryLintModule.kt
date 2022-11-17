package com.mercadolibre.android.gradle.library.core.action.modules.lint

import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMPILE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.IMPLEMENTATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_TASK
import org.gradle.api.Project

/**
 * This class takes care of the Lint of the App type modules.
 */
class LibraryLintModule : LintableModule(LINTABLE_TASK, LINTABLE_DESCRIPTION) {

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(project: Project): Lint {
        val variantNames = arrayListOf<String>()
        findExtension<LibraryExtension>(project)?.apply {
            libraryVariants.all {
                val variantName = this.name
                variantNames.add("${variantName}${IMPLEMENTATION_CONSTANT.capitalize()}")
                variantNames.add("${variantName}${API_CONSTANT.capitalize()}")
                variantNames.add("${variantName}${COMPILE_CONSTANT.capitalize()}")
            }
        }
        variantNames.add(API_CONSTANT)
        variantNames.add(IMPLEMENTATION_CONSTANT)
        variantNames.add(COMPILE_CONSTANT)

        return LibraryAllowListDependenciesLint(variantNames)
    }
}
