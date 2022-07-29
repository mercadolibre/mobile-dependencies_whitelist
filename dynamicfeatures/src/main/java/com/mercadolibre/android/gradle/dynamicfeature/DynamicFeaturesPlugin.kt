package com.mercadolibre.android.gradle.dynamicfeature

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractPlugin
import com.mercadolibre.android.gradle.baseplugin.core.components.DYNAMIC_FEATURE_PLUGINS
import com.mercadolibre.android.gradle.dynamicfeature.core.action.configurers.DynamicFeatureAndroidConfigurer

/**
 * BaseAppPlugin is in charge of configuring the Dynamic Feature module of the repository where it is being applied.
 */
open class DynamicFeaturesPlugin : AbstractPlugin() {

    /**
     * This variable contains the configurers that will be executed when applying the plugin.
     */
    override val configurers = arrayListOf(
        PluginConfigurer(DYNAMIC_FEATURE_PLUGINS),
        DynamicFeatureAndroidConfigurer()
    )
}
