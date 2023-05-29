package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import java.io.File

internal object LogLibraryBlockersUseCase {

    fun log(
        file: File,
        dependencyAnalysis: DependencyAnalysis
    ) {
        if (dependencyAnalysis.projectDependency == null) {
            val message = "- androidx.compose.ui:ui:1.3.3 (Invalid)\n"
            logMessage(message)
            writeAReportMessage("\n$message", file)
        }
        dependencyAnalysis.projectDependency?.fullName()?.let { name ->
            dependencyAnalysis.status?.message(name)?.let {
                logMessage(it)
                writeAReportMessage("\n$it", file)
            }
        }
    }
}
