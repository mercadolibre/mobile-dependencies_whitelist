package com.mercadolibre.android.gradle.library.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import org.gradle.api.Project

/**
 * The Extensions Configurer is in charge of generating the extensions that the modules request to carry out their operation.
 */
class LibraryExtensionConfigurer : ExtensionsConfigurer() {
    /**
     * This method is responsible for requesting each module to generate the Extension it needs to function correctly.
     */
    override fun configureProject(project: Project) {
        for (extensionProvider in ModuleProvider.provideLibraryAndroidModules()) {
            extensionProvider.createExtension(project)
        }
    }
}
