package com.mercadolibre.android.gradle.app.core.action.modules.bugsnag

import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension

open class BugsnagExtension : ModuleOnOffExtension() {

    override var enabled: Boolean = false
}
