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

    override fun apply(target: Any) = (target as Project).run {
        val appDetection = project.appDetection()

        afterEvaluate {
            KeyStoreModule(appDetection.appDetection.isProductiveApp).configure(project)
        }

        applyBasePlugin(this)
    }

    override val configurers = arrayListOf(
        PluginConfigurer(APP_PLUGINS),
        AndroidConfigurer(),
        AppModuleConfigurer()
    )
}
