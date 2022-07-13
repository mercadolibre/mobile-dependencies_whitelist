package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import org.gradle.api.Project

abstract class Lint: ExtensionGetter() {

    abstract fun name(): String

    abstract fun lint(project: Project, variants: List<BaseVariant>): Boolean

}