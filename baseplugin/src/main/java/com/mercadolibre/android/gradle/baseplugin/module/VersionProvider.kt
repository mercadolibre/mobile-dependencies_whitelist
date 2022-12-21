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

    /**
     * This method is in charge of providing the Min Sdk Version variable.
     */
    fun provideMinSdk(): Int {
        return MIN_SDK_LEVEL
    }

    /**
     * This method is in charge of providing the Api Sdk Level Version variable.
     */
    fun provideApiSdkLevel(): Int {
        return COMPILE_API_SDK_LEVEL
    }

    /**
     * This method is in charge of providing the Gradle Version variable.
     */
    fun gradlewVersion(): Int {
        return GRADLE_VERSION
    }

    /**
     * This method is in charge of providing the Build Tools Version variable.
     */
    fun provideBuildToolsVersion(): String {
        return BUILD_TOOLS_VERSION
    }

    /**
     * This method is in charge of providing the Java Version variable.
     */
    fun provideJavaVersion(): JavaVersion {
        return JAVA_VERSION
    }
}
