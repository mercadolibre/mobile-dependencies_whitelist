package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the operation of the configurers.
 */
interface Configurer {
    fun getDescription(): String
    fun configureProject(project: Project)
}
