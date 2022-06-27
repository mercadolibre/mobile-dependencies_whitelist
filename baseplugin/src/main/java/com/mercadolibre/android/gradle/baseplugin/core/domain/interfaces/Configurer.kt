package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

interface Configurer {
    fun getDescription(): String
    fun configureProject(project: Project)
}