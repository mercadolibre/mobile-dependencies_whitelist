package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the modules so that they configure the project correctly.
 */
interface Module {
    /**
     * This method is responsible for requesting all the modules that can configure a project.
     */
    fun configure(project: Project)
}
