package com.mercadolibre.android.gradle.baseplugin.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider
import com.mercadolibre.android.gradle.baseplugin.core.components.BASICS_CONFIGURER_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.HOURS_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RESOLUTION_STRATEGY_HOURS
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import org.gradle.api.Project

/**
 * The Basics Configurer is in charge of providing the configurations so that the repository is capable of bringing dependencies
 * and managing them.
 */
open class BasicsConfigurer : Configurer {

    private val repositoryProvider by lazy { RepositoryProvider() }

    /**
     * This method allows us to get a description of what this Configurer does.
     */
    override fun getDescription(): String = BASICS_CONFIGURER_DESCRIPTION

    /**
     * This method is responsible for configuring the repositories that a project needs for its dependencies and configures its cache.
     */
    override fun configureProject(project: Project) {
        repositoryProvider.setupFetchingRepositories(project)
        for (subProject in project.subprojects) {
            repositoryProvider.setupFetchingRepositories(subProject)
        }
        setMaxTimeForCachedDynamicVersions(project)
    }

    private fun setMaxTimeForCachedDynamicVersions(project: Project) {
        project.allprojects {
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor(RESOLUTION_STRATEGY_HOURS, HOURS_CONSTANT)
            }
        }
    }
}
