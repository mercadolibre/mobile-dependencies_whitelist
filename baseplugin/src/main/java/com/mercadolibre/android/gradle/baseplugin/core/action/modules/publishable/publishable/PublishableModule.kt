package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable

import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider
import com.mercadolibre.android.gradle.baseplugin.core.components.MAVEN_PUBLISH
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * PublishableModule is in charge of adding the Maven Publication plugin and adding the repositories
 * with the help of ProjectRepositoryConfiguration.
 */
open class PublishableModule : Module() {

    /**
     * This method is responsible for configuring the credentials of the repositories so that we have access to publish
     * and apply the Maven publication plugin.
     */
    override fun configure(project: Project) {
        project.apply(plugin = MAVEN_PUBLISH)

        project.configurations.findByName("archives")?.apply {
            extendsFrom(project.configurations.findByName("default"))
        }

        val repositories = RepositoryProvider().getRepositories()
        ProjectRepositoryConfiguration().setupPublishingRepositories(project, repositories)
    }

    /**
     * This method is in charge of looking up the name of the publication task.
     */
    fun getTaskName(type: String, packaging: String = "", variantName: String = ""): String =
        "publish${packaging.capitalize()}${type}${variantName.capitalize()}"
}
