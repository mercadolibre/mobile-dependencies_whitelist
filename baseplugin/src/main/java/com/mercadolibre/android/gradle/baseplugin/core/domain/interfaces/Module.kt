package com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces

import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.WARNIGN_MESSAGE
import org.gradle.api.Project

/**
 * This interface is responsible for standardizing the modules so that they configure the project correctly.
 */
abstract class Module : ExtensionProvider, ExtensionGetter() {

    /**
     * This method configures the execution time of the module.
     */
    open fun executeInAfterEvaluate(): Boolean = true

    /**
     * This method is responsible for requesting all the modules that can configure a project.
     */
    abstract fun configure(project: Project)

    /**
     * This method is responsible for create the extensions needed for a module.
     */
    override fun createExtension(project: Project) {
        project.extensions.create(getExtensionName(), ModuleOnOffExtension::class.java)
    }

    /**
     * This method is responsible for check the flag of On Off module.
     */
    fun moduleConfiguration(project: Project) {
        if (executeInAfterEvaluate()) {
            executeModule(project)
        } else {
            configure(project)
        }
    }

    /**
     * This method is responsible for execute the configuration of the module.
     */
    open fun executeModule(project: Project) {
        val extension = findOnOffExtension(project, this@Module.getExtensionName())
        if (extension != null) {
            if (extension.enabled) {
                configure(project)
            } else {
                println("$WARNIGN_MESSAGE The ${this@Module.getExtensionName()} is manually disabled in ${project.name} module.")
            }
        } else {
            configure(project)
        }
    }

    override fun getExtensionName(): String = getLowerCaseName()

    private fun getLowerCaseName(): String {
        val className = this::class.java.simpleName
        return className[0].toLowerCase() + className.substring(1, className.length)
    }

    fun findOnOffExtension(project: Project, name: String): ModuleOnOffExtension? = findExtension(project, name) as? ModuleOnOffExtension
}
