package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_REPORT_ERROR

/**
 * The Status Base class is in charge of containing the information obtained from a dependency in the allow list
 * and if it is necessary to report that it has a problem.
 *
 * @param shouldReport This variable represents whether this status should be reported.
 * @param isBlocker This variable represents whether this state is blocking.
 * @param name This variable contains the name of the type of report that.
 * @param message This variable contains the version available if exists.
 */
class StatusBase(val shouldReport: Boolean, val isBlocker: Boolean, val name: String, var message: String?) {
    /**
     * This method is in charge of verifying if it is necessary to report an error or share the deprecation message.
     */
    fun message(dependency: String): String {
        if (!shouldReport) {
            throw IllegalAccessException(LINT_REPORT_ERROR)
        }

        val baseMessage = "- $dependency (${name.toLowerCase().capitalize()})"

        return if (message != null) {
            "$baseMessage ${("Available version $ARROW $message")}"
        } else {
            baseMessage
        }
    }
}
