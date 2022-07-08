package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

/**
 * Application intOption Module is in charge of telling the Lint extension to check dependencies.
 */
class ApplicationLintOptionsModule : Module, ExtensionGetter() {
    /**
     * This metod tell the Lint extension to check dependencies.
     */
    override fun configure(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            lintOptions {
                isCheckDependencies = true
            }
        }
    }
}
