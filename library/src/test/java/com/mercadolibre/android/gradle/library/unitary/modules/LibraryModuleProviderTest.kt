package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.library.module.ModuleProvider

class LibraryModuleProviderTest {

    @org.junit.Test
    fun `When the AppModuleProviderTest is called provide modules`() {
        val modules = ModuleProvider.provideLibraryAndroidModules()

        assert(modules.isNotEmpty())
    }

}