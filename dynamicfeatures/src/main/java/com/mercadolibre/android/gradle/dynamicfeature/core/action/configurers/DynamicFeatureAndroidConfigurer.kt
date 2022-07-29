package com.mercadolibre.android.gradle.dynamicfeature.core.action.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.DEBUG_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.FRESCO_FACEBOOK
import com.mercadolibre.android.gradle.baseplugin.core.components.FRESCO_SOLOADER
import com.mercadolibre.android.gradle.baseplugin.core.components.IMPLEMENTATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.MDS_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RELEASE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.exclude

/**
 * The DynamicFeatureAndroid Configurer is in charge of setting the variables necessary to compile an Dynamic Feature Android module.
 */
open class DynamicFeatureAndroidConfigurer : AndroidConfigurer() {

    /**
     * This method asks all modules to configure the project where the plugin was applied.
     */
    override fun configureProject(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            with(VersionProvider) {
                compileSdkVersion(provideApiSdkLevel())

                defaultConfig {
                    setMinSdkVersion(provideMinSdk())
                }

                buildTypes {
                    (findByName(RELEASE_CONSTANT) ?: create(RELEASE_CONSTANT)).minifyEnabled(false)
                    (findByName(DEBUG_CONSTANT) ?: create(DEBUG_CONSTANT)).debuggable(true)
                    (findByName(MDS_CONSTANT) ?: create(MDS_CONSTANT)).debuggable(true)
                }

                project.allprojects {
                    compileOptions {
                        sourceCompatibility = provideJavaVersion()
                        targetCompatibility = provideJavaVersion()
                    }
                }
            }
        }

        with(project.configurations.findByName(IMPLEMENTATION_CONSTANT)!!) {
            exclude(group = FRESCO_FACEBOOK, module = "webpsupport")
            exclude(group = FRESCO_SOLOADER, module = "soloader")
            exclude(group = FRESCO_FACEBOOK, module = "soloader")
            exclude(group = FRESCO_FACEBOOK, module = "nativeimagefilters")
            exclude(group = FRESCO_FACEBOOK, module = "nativeimagetranscoder")
            exclude(group = FRESCO_FACEBOOK, module = "memory-type-native")
            exclude(group = FRESCO_FACEBOOK, module = "imagepipeline-native")
        }
    }
}
