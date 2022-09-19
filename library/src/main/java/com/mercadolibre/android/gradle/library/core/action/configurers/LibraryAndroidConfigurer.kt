package com.mercadolibre.android.gradle.library.core.action.configurers

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import org.gradle.api.Project

/**
 * The Library Android Configurer is in charge of setting the variables necessary to compile an Android module.
 */
class LibraryAndroidConfigurer : AndroidConfigurer() {

    /**
     * This is the method that sets all the variables needed to build an android project.
     */
    override fun configureProject(project: Project) {
        super.configureProject(project)

        findExtension<BaseExtension>(project)?.apply {
            with(defaultConfig) {
                consumerProguardFiles("proguard-rules.pro")
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }
    }
}
