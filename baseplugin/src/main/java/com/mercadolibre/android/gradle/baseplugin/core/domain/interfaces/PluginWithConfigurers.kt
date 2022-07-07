package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

/**
 * This interface is responsible for standardizing the use of configurers within plugins.
 */
interface PluginWithConfigurers {
    val configurers: ArrayList<Configurer>
}
