package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_PRINT_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi

/**
 * Version container is in charge of storing the versions where the experimental will be published, so that it will be implemented.
 */
class VersionContainer {

    private val map = mutableMapOf<String, String>()

    private fun key(projectName: String, publicationName: String): String {
        return "$projectName:$publicationName"
    }

    fun put(projectName: String, publicationName: String, version: String) {
        map[key(projectName, publicationName)] = version
    }

    fun get(projectName: String, publicationName: String, defaultValue: String): String {
        return if (map[key(projectName, publicationName)] != null) {
            map[key(projectName, publicationName)]!!
        } else {
            defaultValue
        }
    }

    fun logVersion(version: String) {
        println("\n$PUBLISHING_PRINT_MESSAGE$version\n".ansi(ANSI_GREEN))
    }
}
