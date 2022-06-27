package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

interface ExtensionProvider {
    fun createExtension(project: Project)
    fun getName(): String
}