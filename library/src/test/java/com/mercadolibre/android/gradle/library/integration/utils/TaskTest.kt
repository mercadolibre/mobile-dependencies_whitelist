package com.mercadolibre.android.gradle.library.integration.utils

import org.gradle.api.Project

abstract class TaskTest {

    fun findExtension(name: String, project: Project): Boolean {
        return project.extensions.findByName(name) != null
    }

}