package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.ExtensionProvider
import org.gradle.api.Project

class LintExtension: ExtensionProvider {

    override fun getName(): String {
        return LINTABLE_EXTENSION
    }

    override fun createExtension(project: Project) {
        project.extensions.create(getName(), LintGradleExtension::class.java)
        for (subProject in project.subprojects) {
            subProject.extensions.create(getName(), LintGradleExtension::class.java)
        }
    }
}