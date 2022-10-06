package com.mercadolibre.android.gradle.baseplugin.core.action.providers

import com.mercadolibre.android.gradle.baseplugin.core.components.ERROR_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.WARNIGN_MESSAGE

/**
 * This object is responsible for send messages in console.
 */
object OutPutUtils {

    /**
     * This method is responsible for send multiple error messages in console.
     */
    fun sendMultipleErrorMessages(messages: List<String>) {
        for (message in messages) {
            sendAErrorMessage(message)
        }
    }

    /**
     * This method is responsible for send multiple warning messages in console.
     */
    fun sendMultipleWarningMessages(messages: List<String>) {
        for (message in messages) {
            sendAWarningMessage(message)
        }
    }

    /**
     * This method is responsible for send one warning message in console.
     */
    fun sendAWarningMessage(message: String) {
        println("$WARNIGN_MESSAGE $message")
    }

    /**
     * This method is responsible for send one error message in console.
     */
    fun sendAErrorMessage(message: String) {
        println("$ERROR_MESSAGE $message")
    }
}
