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

    /**
     * This method is in charge of storing a release version.
     */
    fun put(projectName: String, publicationName: String, version: String) {
        map[key(projectName, publicationName)] = version
    }

    /**
     * This method is in charge of provide a release version safely.
     */
    fun get(projectName: String, publicationName: String, defaultValue: String): String {
        return if (map[key(projectName, publicationName)] != null) {
            map[key(projectName, publicationName)]!!
        } else {
            defaultValue
        }
    }

    /**
     * This method is in charge of showing in the console the version ready to implement.
     */
    fun logVersion(version: String) {
        println("\n$PUBLISHING_PRINT_MESSAGE$version\n".ansi(ANSI_GREEN))
    }
}
