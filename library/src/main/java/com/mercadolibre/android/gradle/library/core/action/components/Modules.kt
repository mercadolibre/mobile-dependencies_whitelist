package com.mercadolibre.android.gradle.library.core.action.components

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.LintableModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import com.mercadolibre.android.gradle.library.core.action.modules.plugin_description.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.core.action.modules.testeable.LibraryTestableModule

internal val ANDROID_LIBRARY_MODULES =
    listOf<Module>(
        LintableModule(),
        LibraryJacocoModule(),
        LibraryTestableModule(),
        LibraryPublishableModule(),
        LibraryPluginDescriptionModule()
    )