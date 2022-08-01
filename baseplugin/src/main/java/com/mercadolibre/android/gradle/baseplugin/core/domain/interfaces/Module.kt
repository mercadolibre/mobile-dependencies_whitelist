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
     * This method is responsible for requesting all the modules that can configure a project.
     */
    abstract fun configure(project: Project)

    /**
     * This method is responsible for create the extensions needed for a module.
     */
    override fun createExtension(project: Project) {
        createOnOffExtension(project)
    }

    /**
     * This method is responsible for check the flag of On Off module.
     */
    fun moduleConfiguration(project: Project) {
        project.afterEvaluate {
            val extension = findOnOffExtension(project, this@Module.getExtensionName())
            if (extension != null) {
                if (extension.enabled) {
                    configure(project)
                } else {
                    println("$WARNIGN_MESSAGE The ${this@Module::class.java.simpleName} is manually disabled in ${project.name} module.")
                }
            } else {
                configure(project)
            }
        }
    }

    override fun getExtensionName(): String = getLowerCaseName()

    private fun getLowerCaseName(): String =
        this::class.java.simpleName[0].toLowerCase() + this::class.java.simpleName.substring(1, this::class.java.simpleName.length)

    private fun createOnOffExtension(project: Project) {
        project.extensions.create(getLowerCaseName(), ModuleOnOffExtension::class.java)
    }

    private fun findOnOffExtension(project: Project, name: String): ModuleOnOffExtension? =
        findExtension(project, name) as? ModuleOnOffExtension
}
