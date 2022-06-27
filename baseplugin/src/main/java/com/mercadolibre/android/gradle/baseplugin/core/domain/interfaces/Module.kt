package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import org.gradle.api.Project

interface Module {
    fun configure(project: Project)
}