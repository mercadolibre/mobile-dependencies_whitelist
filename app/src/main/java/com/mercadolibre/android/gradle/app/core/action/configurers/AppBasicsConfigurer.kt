package com.mercadolibre.android.gradle.app.core.action.configurers

import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Configurer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails

/**
 * This clas is responsible for configure the app module dependencies.
 */
class AppBasicsConfigurer : Configurer {

    /**
     * This method is responsible for provide the Configurer descripiton.
     */
    override fun getDescription(): String =
        "This configurer is in charge of forcing the dependencies so that the project does not generate repeated outputs."

    /**
     * This method is responsible for configure the dependencies.
     */
    override fun configureProject(project: Project) {
        project.rootProject.beforeEvaluate {
            setConfigurations(this)
        }
    }

    /**
     * This method is responsible for set the configurations.
     */
    fun setConfigurations(project: Project) {
        project.configurations.all {
            configResolutionStrategy(this)
        }
    }

    /**
     * This method is responsible for set the dependency resolve details.
     */
    fun configResolutionStrategy(configuration: Configuration) {
        with(configuration) {
            resolutionStrategy.eachDependency {
                configureDependencies(this)
            }
        }
    }

    /**
     * This method is responsible for set the org.ow2.asm version.
     */
    fun configureDependencies(dependency: DependencyResolveDetails) {
        with(dependency) {
            if (requested.group == "org.ow2.asm") {
                useVersion("7.0")
                because("Version required by Firebase Performance Plugin")
            }
        }
    }
}
