package com.mercadolibre.android.gradle.baseplugin.module

import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_TOOLS_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.COMPILE_API_SDK_LEVEL
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.JAVA_VERSION
import com.mercadolibre.android.gradle.baseplugin.core.components.MIN_SDK_LEVEL
import org.gradle.api.JavaVersion

/**
 * VersionProvider is in charge of providing the versions that the AndroidConfigurer will use to configure the android modules.
 */
object VersionProvider {

    fun provideMinSdk(): Int {
        return MIN_SDK_LEVEL
    }

    fun provideApiSdkLevel(): Int {
        return COMPILE_API_SDK_LEVEL
    }

    fun gradlewVersion(): Int {
        return GRADLE_VERSION
    }

    fun provideBuildToolsVersion(): String {
        return BUILD_TOOLS_VERSION
    }

    fun provideJavaVersion(): JavaVersion {
        return JAVA_VERSION
    }
}
