package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.module.ModuleProvider

class AppModuleProviderTest {

    @org.junit.Test
    fun `When the AppModuleProviderTest is called provide modules`() {
        ModuleProvider.provideAppAndroidModules()
    }

}