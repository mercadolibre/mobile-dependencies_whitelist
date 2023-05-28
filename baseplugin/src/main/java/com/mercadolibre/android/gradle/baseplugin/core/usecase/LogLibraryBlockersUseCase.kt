package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import java.io.File

internal object LogLibraryBlockersUseCase {

    fun log(
        file: File,
        dependencyAnalysis: DependencyAnalysis
    ) {
        val message = dependencyAnalysis.dependency.fullName()
        logMessage(message)
        writeAReportMessage("\n$message", file)
    }
}
