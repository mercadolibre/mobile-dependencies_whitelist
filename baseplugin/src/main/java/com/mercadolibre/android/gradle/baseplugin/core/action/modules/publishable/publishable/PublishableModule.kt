package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable

import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

open class PublishableModule: Module, ExtensionGetter() {

    override fun configure(project: Project) {
        project.apply(plugin = "org.gradle.maven-publish")

        project.configurations.findByName("archives")?.apply {
            extendsFrom(project.configurations.findByName("default"))
        }

        val repositories = RepositoryProvider().getRepositories()
        ProjectRepositoryConfiguration().setupPublishingRepositories(project, repositories)
    }

    fun getTaskName(type: String, packaging: String = "", variantName: String = ""): String {
        return "publish${packaging.capitalize()}${type}${variantName.capitalize()}"
    }

}