package com.mercadolibre.android.gradle.baseplugin

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractPlugin

/**
 * BasePlugin is in charge of configuring the root project of the repository where it is being applied.
 */
class BasePlugin : AbstractPlugin() {

    /**
     * This variable contains the configurers that will be executed when applying the plugin.
     */
    override val configurers = arrayListOf(
        BasicsConfigurer(),
        ExtensionsConfigurer(),
        ModuleConfigurer()
    )
}
