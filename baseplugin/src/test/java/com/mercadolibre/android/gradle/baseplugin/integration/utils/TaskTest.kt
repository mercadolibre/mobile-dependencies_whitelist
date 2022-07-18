package com.mercadolibre.android.gradle.baseplugin.integration.utils

import org.gradle.api.Project

open class TaskTest {
    fun findExtension(name: String, project: Project): Boolean = project.extensions.findByName(name) != null
}
