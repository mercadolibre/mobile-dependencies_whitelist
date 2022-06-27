package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.initialization.Settings

interface SettingsModule {
    fun configure(settings: Settings)
}