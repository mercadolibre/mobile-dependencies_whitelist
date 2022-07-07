package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the modules that provide an extension, so that they are applied automatically.
 */
interface ExtensionProvider {
    fun createExtension(project: Project)
    fun getName(): String
}
