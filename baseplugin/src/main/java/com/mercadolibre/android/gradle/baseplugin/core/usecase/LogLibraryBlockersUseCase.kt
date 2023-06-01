package com.mercadolibre.android.gradle.baseplugin.core.usecase

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyAnalysis
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.logMessage
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils.writeAReportMessage
import com.mercadolibre.android.gradle.baseplugin.core.extensions.fullName
import com.mercadolibre.android.gradle.baseplugin.core.extensions.id
import java.io.File

internal object LogLibraryBlockersUseCase {

    fun log(
        file: File,
        dependencyAnalysis: DependencyAnalysis
    ) {
        dependencyAnalysis.apply {
            projectDependency?.let { log(it.fullName(), file) }
            notFound.takeIf { it.isNotEmpty() }?.let { name -> log(name, file) }
        }
    }

    private fun DependencyAnalysis.log(
        name: String,
        file: File
    ) {
        status?.message(name)?.let {
            logMessage(it)
            writeAReportMessage("\n$it", file)
        }
    }
}
