package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildCache.BuildCacheModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan.BuildScanModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.dexcount.DexCountModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.JavaJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library.JavaLintModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants.ListVariantsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.PluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectInfo.ProjectInfoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectVersion.ProjectVersionModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.JavaPublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule

internal val JAVA_MODULES =
    listOf<Module>(
        JavaJacocoModule(),
        JavaPublishableModule(),
        JavaLintModule()
    )

internal val PROJECT_MODULES =
    listOf<Module>(
        ListProjectsModule(),
        ListVariantsModule(),
        ProjectInfoModule(),
        PluginDescriptionModule(),
        ProjectVersionModule(),
        DexCountModule()
    )

internal val SETTINGS_MODULES =
    listOf<SettingsModule>(
        BuildCacheModule(),
        BuildScanModule()
    )
