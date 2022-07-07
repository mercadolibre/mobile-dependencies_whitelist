package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan.BuildScanModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.JavaJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.ListProjectsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listVariants.ListVariantsModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.plugin_description.PluginDescriptionModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.project_version.ProjectVersionModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.JavaPublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule

internal val JAVA_MODULES =
    listOf<Module>(
        JavaJacocoModule(),
        JavaPublishableModule()
    )

internal val PROJECT_MODULES =
    listOf<Module>(
        ListProjectsModule(),
        ListVariantsModule(),
        PluginDescriptionModule(),
        ProjectVersionModule()
    )

internal val SETTINGS_MODULES =
    listOf<SettingsModule>(
        BuildScanModule()
    )
