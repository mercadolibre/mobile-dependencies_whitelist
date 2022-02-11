package com.mercadolibre.android.gradle.base.modules;

import org.gradle.api.initialization.Settings;

/**
 * Base interface of a settings module
 * <p>
 * Created by mafunes on 10/08/20.
 */
interface SettingsModule {
    void configure(Settings settings);
}
