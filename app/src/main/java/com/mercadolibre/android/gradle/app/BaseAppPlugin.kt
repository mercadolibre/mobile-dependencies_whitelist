package com.mercadolibre.android.gradle.app

import com.mercadolibre.android.gradle.app.core.action.configurers.AppBasicsConfigurer
import com.mercadolibre.android.gradle.app.core.action.configurers.AppExtensionConfigurer
import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractProjectPlugin
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS

/**
 * BaseAppPlugin is in charge of configuring the app module of the repository where it is being applied.
 */
class BaseAppPlugin : AbstractProjectPlugin() {

    /**
     * This variable contains the configurers that will be executed when applying the plugin.
     */
    override val configurers = arrayListOf(
        PluginConfigurer(APP_PLUGINS),
        AndroidConfigurer(),
        AppExtensionConfigurer(),
        AppModuleConfigurer(),
        AppBasicsConfigurer()
    )
}
