package com.mercadolibre.android.gradle.app

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.core.action.modules.keystore.KeyStoreModule
import com.mercadolibre.android.gradle.app.module.TypeAppDetection.Companion.appDetection
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.basics.AbstractPlugin
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import org.gradle.api.Project

/**
 * BaseAppPlugin is in charge of configuring the app module of the repository where it is being applied.
 */
class BaseAppPlugin : AbstractPlugin() {

    /**
     * This method is responsible for applying all the plugin settings in a root or in a settings.
     */
    override fun apply(target: Any) {
        if (target is Project) {
            val appDetection = target.appDetection()

            target.afterEvaluate {
                KeyStoreModule(appDetection.appDetection.isProductiveApp).configure(this)
            }
        }
        super.apply(target)
    }

    /**
     * This variable contains the configurers that will be executed when applying the plugin.
     */
    override val configurers = arrayListOf(
        PluginConfigurer(APP_PLUGINS),
        AndroidConfigurer(),
        AppModuleConfigurer()
    )
}
