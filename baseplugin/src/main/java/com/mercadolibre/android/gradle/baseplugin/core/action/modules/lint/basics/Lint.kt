package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import org.gradle.api.Project

/**
 * This interface is in charge of giving us the possibility of pointing the linteo to different types of module
 * but with the same functionality.
 */
abstract class Lint : ExtensionGetter() {

    /**
     * This method is responsible for providing a name to the linteo class.
     */
    abstract fun name(): String

    /**
     * This method is responsible for providing the functionality of checking dependencies.
     */
    abstract fun lint(project: Project, variants: ArrayList<BaseVariant>): Boolean
}
