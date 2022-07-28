package com.mercadolibre.android.gradle.baseplugin.unitary.modules.buildCache

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildCache.BuildCacheModule
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.initialization.Settings
import org.gradle.caching.configuration.BuildCacheConfiguration
import org.gradle.caching.http.HttpBuildCache
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BuildCacheModuleTest : AbstractPluginManager() {

    private val buildCache = BuildCacheModule()

    @org.junit.Test
    fun `When the BuildCacheModuleTest is called configure the setting`() {
        val configuration = mockk<BuildCacheConfiguration>() {
            every { local.setEnabled(true) } returns mockk(relaxed = true)
            every { remote(HttpBuildCache::class.java) } returns mockk(relaxed = true)
        }
        val settings = mockk<Settings>(relaxed = true) {
            every { buildCache } returns configuration
        }
        buildCache.configure(settings)
    }
}
