package com.mercadolibre.android.gradle.baseplugin

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractPlugin

/**
 * BasePlugin is in charge of configuring the root project of the repository where it is being applied.
 */
class BasePlugin : AbstractPlugin() {

    override val configurers = arrayListOf(
        BasicsConfigurer(),
        ExtensionsConfigurer(),
        ModuleConfigurer()
    )
}
