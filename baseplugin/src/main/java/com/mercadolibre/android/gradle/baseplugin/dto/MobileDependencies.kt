package com.mercadolibre.android.gradle.baseplugin.dto

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency

data class MobileDependencies(
    val whitelist: List<Dependency>
)