package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logError
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_POSTDATA_MESSAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_TITLE
import java.io.File

private const val URL_PLACEHOLDER = "<URL>"

internal object LogLibraryBlockersComplianceUseCase {

    fun log(file: File, url: String, messages: () -> Unit) {
        logError(LINT_ERROR_TITLE)
        writeAReportMessage(LINT_ERROR_TITLE, file)

        messages()

        val baseAllowListMessage = LINT_ERROR_POSTDATA_MESSAGE.replace(URL_PLACEHOLDER, url)
        logMessage("\n$baseAllowListMessage")
        writeAReportMessage("\n$baseAllowListMessage", file)
    }
}
