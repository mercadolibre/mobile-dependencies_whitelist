package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider
import org.gradle.api.Project

/**
 * The Android Configurer is in charge of setting the variables necessary to compile an Android module.
 */
open class AndroidConfigurer : Configurer, ExtensionGetter() {

    /**
     * This method allows us to get a description of what this Configurer does.
     */
    override fun getDescription(): String = ANDROID_CONFIGURER_DESCRIPTION

    /**
     * This is the method that sets all the variables needed to build an android project.
     */
    override fun configureProject(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            with(VersionProvider) {
                compileSdkVersion(provideApiSdkLevel())
                buildToolsVersion(provideBuildToolsVersion())

                with(defaultConfig) {
                    setMinSdkVersion(provideMinSdk())
                    setTargetSdkVersion(provideApiSdkLevel())
                }

                project.allprojects {
                    compileOptions {
                        sourceCompatibility = provideJavaVersion()
                        targetCompatibility = provideJavaVersion()
                    }
                }
            }
        }
    }
}
