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

    override fun getDescription(): String {
        return ANDROID_CONFIGURER_DESCRIPTION
    }

    override fun configureProject(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            with(VersionProvider) {
                compileSdkVersion(provideApiSdkLevel())
                buildToolsVersion(provideBuildToolsVersion())

                defaultConfig {
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
