package com.mercadolibre.android.gradle.baseplugin.core.basics

import org.gradle.api.plugins.ExtensionAware

abstract class ExtensionGetter {

    inline fun <reified T>findExtension(obj: ExtensionAware): T? {
        return obj.extensions.findByType(T::class.java)
    }

    fun findExtension(obj: ExtensionAware, name: String): Any? {
        return obj.extensions.findByName(name)
    }
}