package com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies

import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_REPORT_ERROR

class StatusBase(val shouldReport: Boolean, val isBlocker: Boolean, val name: String) {
    fun message(dependency: String): String {
        if (!shouldReport) {
            throw IllegalAccessException(LINT_REPORT_ERROR)
        }
        return "- $dependency (${name.toLowerCase().capitalize()})"
    }
}
