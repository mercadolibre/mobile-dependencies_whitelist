package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import java.io.File

internal object LogLibraryWarningsUseCase {

    /**
     * This method is responsible for generating reports in case there are dependencies that have warnings.
     */
    fun log(file: File, analysis: DependencyAnalysis) {
        analysis.apply {
            val availableVersion = if (availableVersion != null) {
                "Available version $ARROW $availableVersion"
            } else {
                ""
            }
            allowListDependency.apply {
                val message = "($expires) - ${fullName()} (Deprecated!) $availableVersion"
                logMessage(message)
                writeAReportMessage(message, file)
            }
        }
    }
}
