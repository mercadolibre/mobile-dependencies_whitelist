package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.EXTENSIONS_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import org.gradle.api.Project

/**
 * The Extensions Configurer is in charge of generating the extensions that the modules request to carry out their operation.
 */
open class ExtensionsConfigurer : Configurer {

    /**
     * This method allows us to get a description of what this Configurer does.
     */
    override fun getDescription(): String = EXTENSIONS_CONFIGURER_DESCRIPTION

    /**
     * This method allows us to get a description of any Extension.
     */
    fun getExtensions(): String {
        var listOfExtensions = ""

        var extensionsNames = ""
        for (extensionProvider in ModuleProvider.provideAllModules()) {
            extensionsNames += "${extensionProvider.getExtensionName()}, "
        }
        listOfExtensions += " $ARROW " + extensionsNames.substring(0, extensionsNames.length - 2).ansi(ANSI_GREEN)
        return listOfExtensions
    }

    /**
     * This method is responsible for requesting each module to generate the Extension it needs to function correctly.
     */
    override fun configureProject(project: Project) {
        for (module in ModuleProvider.provideAllModules()) {
            module.createExtension(project)
        }
    }
}
