package com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies

data class Dependency(
    val group: String?,
    val name: String?,
    val version: String?,
    val expires: Long?,
    val rawExpiresDate: String?,
)
