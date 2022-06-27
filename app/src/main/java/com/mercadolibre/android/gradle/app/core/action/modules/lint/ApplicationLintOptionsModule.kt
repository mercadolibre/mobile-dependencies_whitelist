package com.mercadolibre.android.gradle.app.core.action.modules.lint

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

class ApplicationLintOptionsModule: Module, ExtensionGetter() {
    override fun configure(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            lintOptions {
                isCheckDependencies = true
            }
        }
    }
}