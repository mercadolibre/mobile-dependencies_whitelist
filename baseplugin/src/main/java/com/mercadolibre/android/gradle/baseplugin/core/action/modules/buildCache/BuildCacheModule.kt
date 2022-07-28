package com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildCache

import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI_GRADLE_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_CI_GRADLE_USER
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CACHE_URL
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.SettingsModule
import org.gradle.api.initialization.Settings
import org.gradle.caching.configuration.BuildCacheConfiguration
import org.gradle.caching.http.HttpBuildCache
import java.net.URI

/**
 * This class is responsible for adding the functionality of the Build Cache.
 */
class BuildCacheModule : SettingsModule {
    override fun configure(settings: Settings) {
        configureBuildCache(settings.buildCache)
    }

    /**
     * This method is responsible for setting the build cache.
     */
    fun configureBuildCache(configuration: BuildCacheConfiguration) {
        with(configuration) {
            local.isEnabled = true
            configureHttpBuildCache(remote(HttpBuildCache::class.java))
        }
    }

    /**
     * This method takes care of configuring the remote build cache.
     */
    fun configureHttpBuildCache(httpBuildCache: HttpBuildCache) {
        with(httpBuildCache) {
            url = URI(BUILD_CACHE_URL)
            isPush = System.getenv().containsKey(BUILD_CACHE_CI)
            credentials.username = System.getenv(BUILD_CACHE_CI_GRADLE_USER)
            credentials.password = System.getenv(BUILD_CACHE_CI_GRADLE_PASSWORD)
        }
    }
}
