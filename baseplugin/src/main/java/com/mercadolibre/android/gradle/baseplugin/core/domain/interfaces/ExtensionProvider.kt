package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the modules that provide an extension, so that they are applied automatically.
 */
interface ExtensionProvider {
    /**
     * This method is in charge of requesting the classes that want to provide an extension to contain this functionality.
     */
    fun createExtension(project: Project)
    /**
     * This method is in charge of asking the classes that want to provide an extension to have a name for it.
     */
    fun getName(): String
}
