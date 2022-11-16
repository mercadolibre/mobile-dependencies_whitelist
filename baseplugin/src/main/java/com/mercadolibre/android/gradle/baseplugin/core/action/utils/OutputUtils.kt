package com.mercadolibre.android.gradle.baseplugin.core.action.utils

import com.mercadolibre.android.gradle.baseplugin.core.components.ERROR_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.WARNIGN_MESSAGE
import java.io.File

/**
 * This object is responsible for send messages in console.
 */
object OutputUtils {

    /**
     * This method is responsible for write a report in a file.
     */
    fun writeAReportMessage(message: String, file: File) {
        writeInAReport(message, createFileIfNotExist(file))
    }

    private fun createFileIfNotExist(file: File): File {
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
        return file
    }

    private fun writeInAReport(message: String, file: File) {
        file.appendText(message + "\n")
    }

    /**
     * This method is responsible for send one message in console.
     */
    fun logMessage(message: String) {
        println(message)
    }

    /**
     * This method is responsible for send one message in console with one Error subfix.
     */
    fun logError(message: String) {
        logAErrorMessage(message)
    }

    /**
     * This method is responsible for send one message in console with one Warning subfix.
     */
    fun logWarning(message: String) {
        logAWarningMessage(message)
    }

    /**
     * This method is responsible for send multiple messages in console.
     */
    fun logMessage(message: List<String>) {
        for (lineMessage in message) {
            logMessage(lineMessage)
        }
    }

    /**
     * This method is responsible for send multiple messages in console with one Warning subfix.
     */
    fun logWarning(message: List<String>) {
        for (lineMessage in message) {
            logWarning(lineMessage)
        }
    }

    /**
     * This method is responsible for send multiple messages in console with one Error subfix.
     */
    fun logError(message: List<String>) {
        for (lineMessage in message) {
            logError(lineMessage)
        }
    }

    private fun logAWarningMessage(message: String) {
        logMessage("$WARNIGN_MESSAGE $message")
    }

    private fun logAErrorMessage(message: String) {
        logMessage("$ERROR_MESSAGE $message")
    }
}
