package com.mercadolibre.android.gradle.baseplugin.core.basics

import org.gradle.api.plugins.ExtensionAware

/**
 * ExtensionGetter is in charge of providing the functionality to search for extensions within a project.
 */
open class ExtensionGetter {

    /**
     * This method is in charge of looking for an extension by its type within an object that can contain extensions..
     */
    inline fun <reified T> findExtension(obj: ExtensionAware): T? = obj.extensions.findByType(T::class.java)

    /**
     * This method is in charge of looking for an extension by its name within an object that can contain extensions.
     */
    fun findExtension(obj: ExtensionAware, name: String): Any? = obj.extensions.findByName(name)
}
