package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * This class takes care of the Lint of the App type modules.
 */
class JavaLintModule : LintableModule() {

    /**
     * This method is in charge of providing the object that the Lint will do.
     */
    override fun getLinter(project: Project): Lint {
        val sourceSets = arrayListOf<SourceSet>()
        findExtension<SourceSetContainer>(project)?.apply {
            all {
                sourceSets.add(this)
            }
        }
        return LibraryAllowListDependenciesLint(sourceSets.map { it.name })
    }
}
