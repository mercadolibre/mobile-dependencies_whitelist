package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logWarning
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_TITLE
import java.io.File

internal object LogLibraryWarningsComplianceUseCase {

    fun log(file: File, messages: () -> Unit) {
        writeAReportMessage(LINT_WARNIGN_TITLE, file)
        logWarning(LINT_WARNIGN_TITLE)
        messages()
        logMessage(LINT_WARNIGN_DESCRIPTION)
        writeAReportMessage(LINT_WARNIGN_DESCRIPTION, file)
    }
}