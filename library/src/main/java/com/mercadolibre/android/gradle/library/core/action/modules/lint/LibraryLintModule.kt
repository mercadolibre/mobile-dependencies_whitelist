package com.mercadolibre.android.gradle.library.core.action.modules.lint

import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMPILE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.IMPLEMENTATION_CONSTANT
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

/**
 * This class takes care of the Lint of the App type modules.
 */
class LibraryLintModule : LintableModule() {

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(project: Project): Lint {
        val variantNames = arrayListOf<String>()
        findExtension<LibraryExtension>(project)?.apply {
            libraryVariants.all {
                val variantName = this.name
                variantNames.add("${variantName}${IMPLEMENTATION_CONSTANT.capitalized()}")
                variantNames.add("${variantName}${API_CONSTANT.capitalized()}")
                variantNames.add("${variantName}${COMPILE_CONSTANT.capitalized()}")
            }
        }
        variantNames.add(API_CONSTANT)
        variantNames.add(IMPLEMENTATION_CONSTANT)
        variantNames.add(COMPILE_CONSTANT)

        return LibraryAllowListDependenciesLint(variantNames)
    }
}
