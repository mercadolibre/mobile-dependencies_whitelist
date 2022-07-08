package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.initialization.Settings

/**
 * This interface is responsible for standardizing the modules so that they configurer settings correctly.
 */
interface SettingsModule {
    /**
     * This method is responsible for requesting all the modules that can configure a settings.
     */
    fun configure(settings: Settings)
}
