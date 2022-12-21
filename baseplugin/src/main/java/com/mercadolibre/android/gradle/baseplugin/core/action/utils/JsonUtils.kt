package com.mercadolibre.android.gradle.baseplugin.core.action.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat

/**
 * This object is responsible for manage Json.
 */
object JsonUtils {

    private val DATE_PATTERN = "yyyy-MM-dd"

    fun getJson(url: String): JsonObject {
        return stringToJsonObject(getInputStream(url))
    }

    fun getJson(file: File): JsonObject {
        return stringToJsonObject(getInputStream(file))
    }

    private fun stringToJsonObject(reader: InputStreamReader): JsonObject {
        return JsonParser.parseReader(reader).asJsonObject
    }

    private fun getInputStream(url: String): InputStreamReader {
        return URL(url).openConnection().getInputStream().reader()
    }

    private fun getInputStream(file: File): InputStreamReader {
        return file.reader()
    }

    /**
     * This method is responsible for obtaining data from a Json safely.
     */
    fun getVariableFromJson(name: String, json: JsonElement, defaultValue: String?): String? {
        return if (json.asJsonObject[name] != null) {
            json.asJsonObject[name].asString.replace("\\", "")
        } else {
            defaultValue
        }
    }

    private fun castStringToDate(date: String): Long {
        return SimpleDateFormat(DATE_PATTERN).parse(date.replace("\\", "")).time
    }

    /**
     * This method is in charge of casting a Json element to a Date in a safe way.
     */
    fun castJsonElementToDate(it: JsonElement): Long? {
        val element = it.asJsonObject[EXPIRES_CONSTANT]
        if (element == null || element.asString == "null") {
            return null
        }
        return castStringToDate(element.asString)
    }
}
