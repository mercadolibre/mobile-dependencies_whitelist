package com.mercadolibre.android.gradle.app.core.action.modules.keystore

import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension

/**
 * This class constains the flag to put on or off Key Store module.
 */
open class KeyStoreExtension : ModuleOnOffExtension() {

    /**
     * This variable constains the flag to put on or off Key Store module.
     */
    override var enabled: Boolean = false
}
