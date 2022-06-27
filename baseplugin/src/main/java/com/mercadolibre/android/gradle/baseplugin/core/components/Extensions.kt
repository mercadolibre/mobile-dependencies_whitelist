package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.domain.BaseJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.ExtensionProvider

val EXTENSIONS_PROVIDERS = listOf<ExtensionProvider>(
    LintableModule(),
    BaseJacocoModule()
)