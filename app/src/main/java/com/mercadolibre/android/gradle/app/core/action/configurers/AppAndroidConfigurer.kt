package com.mercadolibre.android.gradle.app.core.action.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import org.gradle.api.Project

/**
 * The App Android Configurer is in charge of setting the variables necessary to compile an Android module.
 */
class AppAndroidConfigurer : AndroidConfigurer() {

    /**
     * This is the method that sets all the variables needed to build an android project.
     */
    override fun configureProject(project: Project) {
        super.configureProject(project)

        findExtension<BaseExtension>(project)?.apply {
            with(defaultConfig) {
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }
    }
}
