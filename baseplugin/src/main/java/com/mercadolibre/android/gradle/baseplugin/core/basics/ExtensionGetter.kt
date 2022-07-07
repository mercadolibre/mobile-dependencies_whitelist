package com.mercadolibre.android.gradle.baseplugin.core.basics

import org.gradle.api.plugins.ExtensionAware

/**
 * ExtensionGetter is in charge of providing the functionality to search for extensions within a project.
 */
abstract class ExtensionGetter {

    inline fun <reified T> findExtension(obj: ExtensionAware): T? {
        return obj.extensions.findByType(T::class.java)
    }

    fun findExtension(obj: ExtensionAware, name: String): Any? {
        return obj.extensions.findByName(name)
    }
}
