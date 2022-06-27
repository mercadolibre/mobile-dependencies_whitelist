package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

internal class StatusBase(val shouldReport: Boolean, val isBlocker: Boolean){
    fun message(dependency: String, name: String): String {
        if (!shouldReport) {
            throw IllegalAccessException("Cant report this type of dependency")
        }
        return "- $dependency (${name.toLowerCase().capitalize()})"
    }}