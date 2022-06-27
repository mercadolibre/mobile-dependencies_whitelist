package com.mercadolibre.android.gradle.app.core.action.components

import com.mercadolibre.android.gradle.app.core.action.modules.jacoco.AppJacocoModule
import com.mercadolibre.android.gradle.app.core.action.modules.lint.ApplicationLintOptionsModule
import com.mercadolibre.android.gradle.app.core.action.modules.plugin_description.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module

internal val ANDROID_APPLICATION_MODULES =
    listOf<Module>(
        LintableModule(),
        AppJacocoModule(),
        ApplicationLintOptionsModule(),
        AppPluginDescriptionModule()
    )
