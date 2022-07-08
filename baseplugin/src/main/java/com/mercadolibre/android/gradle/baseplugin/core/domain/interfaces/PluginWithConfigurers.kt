package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

/**
 * This interface is responsible for standardizing the use of configurers within plugins.
 */
interface PluginWithConfigurers {
    /**
     * This list contains the Configurers that configure the project where the Plugin is applied.
     */
    val configurers: ArrayList<Configurer>
}
