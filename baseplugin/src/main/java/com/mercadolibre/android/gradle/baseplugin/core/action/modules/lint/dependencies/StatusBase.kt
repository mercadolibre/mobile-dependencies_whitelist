package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_REPORT_ERROR

/**
 * The Status Base class is in charge of containing the information obtained from a dependency in the allow list
 * and if it is necessary to report that it has a problem.
 *
 * @param shouldReport This variable represents whether this status should be reported.
 * @param isBlocker This variable represents whether this state is blocking.
 * @param name This variable contains the name of the type of report that.
 */
class StatusBase(val shouldReport: Boolean, val isBlocker: Boolean, val name: String) {
    /**
     * This method is responsible for providing the error message when the dependency is not valid.
     */
    fun message(dependency: String): String {
        if (!shouldReport) {
            throw IllegalAccessException(LINT_REPORT_ERROR)
        }
        return "- $dependency (${name.toLowerCase().capitalize()})"
    }
}
