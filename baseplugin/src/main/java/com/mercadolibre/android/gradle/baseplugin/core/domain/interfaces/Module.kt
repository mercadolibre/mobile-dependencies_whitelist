package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the modules so that they configure the project correctly.
 */
interface Module {
    fun configure(project: Project)
}
