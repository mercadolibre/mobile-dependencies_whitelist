package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

data class Dependency(
    val group: String?,
    val name: String?,
    val version: String?,
    val expires: Long?,
    val rawExpiresDate: String?,
)
