package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.EXTENSIONS_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.EXTENSIONS_PROVIDERS
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import org.gradle.api.Project

/**
 * The Extensions Configurer is in charge of generating the extensions that the modules request to carry out their operation.
 */
open class ExtensionsConfigurer : Configurer {

    override fun getDescription(): String {
        return EXTENSIONS_CONFIGURER_DESCRIPTION
    }

    fun getExtensions(): String {
        var listOfExtensions = ""

        for (extensionProvider in EXTENSIONS_PROVIDERS) {
            listOfExtensions += "- " +
                extensionProvider::class.java.simpleName.ansi(ANSI_YELLOW) +
                " $ARROW " +
                extensionProvider.getName().ansi(ANSI_GREEN)
        }

        return listOfExtensions
    }

    override fun configureProject(project: Project) {
        for (extensionProvider in EXTENSIONS_PROVIDERS) {
            extensionProvider.createExtension(project)
        }
    }
}
