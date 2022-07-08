package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the operation of the configurers.
 */
interface Configurer {
    /**
     * This method is in charge of requesting all the configurers that have a description
     */
    fun getDescription(): String

    /**
     * This method is in charge of requesting all the configurers that can configure a project
     */
    fun configureProject(project: Project)
}
