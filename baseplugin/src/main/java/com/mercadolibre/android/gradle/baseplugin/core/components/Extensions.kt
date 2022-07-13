package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain.BaseJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintExtension

val EXTENSIONS_PROVIDERS = listOf(
    LintExtension(),
    BaseJacocoModule()
)