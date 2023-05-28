package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.google.gson.Gson
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.dto.MobileDependencies
import java.net.URL

internal object GetAllowedDependenciesUseCase {

    fun get(url: String): List<Dependency> {
        val reader = URL(url).openConnection().getInputStream().reader()
        return Gson().fromJson(reader, MobileDependencies::class.java).whitelist
    }
}