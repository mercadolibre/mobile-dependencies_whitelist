package com.mercadolibre.android.gradle.baseplugin.unitary.modules.buildCache

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildCache.BuildCacheModule
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI_GRADLE_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI_GRADLE_USER
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_URL
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URI
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
        val httpBuildCache = mockk<HttpBuildCache>(relaxed = true)

        val configuration = mockk<BuildCacheConfiguration>() {
            every { local.setEnabled(true) } returns mockk(relaxed = true)
            every { remote(HttpBuildCache::class.java) } returns httpBuildCache
        }
        val settings = mockk<Settings>(relaxed = true) {
            every { buildCache } returns configuration
        }
        buildCache.configure(settings)

        //Configurations set
        verify { configuration.local.isEnabled = true }

        //Http Config
        verify { httpBuildCache.url = URI(BUILD_CACHE_URL) }
        verify { httpBuildCache.isPush = System.getenv().containsKey(BUILD_CACHE_CI) }
    }
}
