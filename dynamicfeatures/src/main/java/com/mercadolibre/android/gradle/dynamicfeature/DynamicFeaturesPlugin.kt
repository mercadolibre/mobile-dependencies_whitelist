package com.mercadolibre.android.gradle.dynamicfeature

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractProjectPlugin
import com.mercadolibre.android.gradle.baseplugin.core.components.KOTLIN_ANDROID
import com.mercadolibre.android.gradle.dynamicfeature.core.action.configurers.DynamicFeatureAndroidConfigurer

/**
 * BaseAppPlugin is in charge of configuring the Dynamic Feature module of the repository where it is being applied.
 */
open class DynamicFeaturesPlugin : AbstractProjectPlugin() {

    private val dynamicFeatures = "com.android.dynamic-feature"

    private val dynamicFeaturesPlugins = listOf(KOTLIN_ANDROID, dynamicFeatures)

    /**
     * This variable contains the configurers that will be executed when applying the plugin.
     */
    override val configurers = arrayListOf(
        PluginConfigurer(dynamicFeaturesPlugins),
        DynamicFeatureAndroidConfigurer()
    )
}
