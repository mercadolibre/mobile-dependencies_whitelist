package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.ARCHIVES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.ARTIFACT_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMPILE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.DEFAULT_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.DEPENDENCIES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.DEPENDENCY_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXCLUSIONS_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXCLUSION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.ID_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.IMPLEMENTATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PROVIDED_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RUNTIME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.SCOPE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.TEST_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import groovy.util.Node
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.configurationcache.extensions.capitalized

/**
 * PomUtils is in charge of generating the Nodes that contain the dependencies of the project so that they
 * can later be read in the publication.
 */
class PomUtils : ExtensionGetter() {

    private fun scope(configuration: String, variantName: String, flavor: String?): String? {
        val compileConfigurations =
            arrayListOf(
                DEFAULT_CONSTANT,
                ARCHIVES_CONSTANT,
                COMPILE_CONSTANT,
                "${variantName}${COMPILE_CONSTANT.capitalized()}",
                IMPLEMENTATION_CONSTANT,
                "${variantName}${IMPLEMENTATION_CONSTANT.capitalized()}",
                API_CONSTANT,
                "${variantName}${API_CONSTANT.capitalized()}"
            )

        if (flavor != null) {
            compileConfigurations.addAll(
                listOf(
                    "${flavor}${API_CONSTANT.capitalized()}",
                    "${flavor}${IMPLEMENTATION_CONSTANT.capitalized()}",
                    "${flavor}${COMPILE_CONSTANT.capitalized()}"
                )
            )
        }

        val testConfigurations = listOf(
            TEST_CONSTANT,
            "$TEST_CONSTANT${variantName.capitalize()}",
            "$TEST_CONSTANT${COMPILE_CONSTANT.capitalized()}",
            "$TEST_CONSTANT${COMPILE_CONSTANT.capitalized()}${variantName.capitalize()}"
        )
        val runtimeConfigurations =
            listOf(
                RUNTIME_CONSTANT,
                "${RUNTIME_CONSTANT}Only",
                "${variantName}${RUNTIME_CONSTANT.capitalized()}", "${variantName}${RUNTIME_CONSTANT.capitalized()}Only"
            )
        val providedConfigurations =
            listOf(
                PROVIDED_CONSTANT,
                "${COMPILE_CONSTANT}Only",
                "${variantName}${COMPILE_CONSTANT.capitalized()}Only"
            )

        return when (configuration) {
            in providedConfigurations -> PROVIDED_CONSTANT
            in runtimeConfigurations -> RUNTIME_CONSTANT
            in testConfigurations -> TEST_CONSTANT
            in compileConfigurations -> COMPILE_CONSTANT
            else -> null
        }
    }

    private fun shouldAddDependency(deps: List<Dependency>, dep: Dependency): Boolean {
        val isWellFormed = dep.group != null && dep.name != null && dep.version != null
        if (!isWellFormed) {
            return false
        }
        return deps.find { it.group == dep.group && it.name == dep.name && it.version == dep.version } == null
    }

    private fun addGroup(node: Node, dependency: Dependency) {
        node.appendNode("$GROUP_CONSTANT${ID_CONSTANT.capitalized()}", dependency.group)
    }

    private fun addName(node: Node, dependency: Dependency) {
        node.appendNode("$ARTIFACT_CONSTANT${ID_CONSTANT.capitalized()}", dependency.name)
    }

    private fun addVersion(node: Node, dependency: Dependency) {
        node.appendNode(VERSION_CONSTANT, dependency.version)
    }

    private fun addScope(node: Node, scope: String) {
        node.appendNode(SCOPE_CONSTANT, scope)
    }

    /**
     * This method is in charge of adding the exclusions of the dependencies to the Node.
     */
    fun addExclusions(node: Node, dependency: ModuleDependency?) {
        if (dependency != null) {
            if (dependency.excludeRules.isNotEmpty()) {
                val exclusionsNode = node.appendNode(EXCLUSIONS_CONSTANT)
                dependency.excludeRules.all { rule ->
                    val exclusionNode = exclusionsNode.appendNode(EXCLUSION_CONSTANT)
                    exclusionNode.appendNode("$GROUP_CONSTANT${ID_CONSTANT.capitalized()}", rule.group)
                    exclusionNode.appendNode("$ARTIFACT_CONSTANT${ID_CONSTANT.capitalized()}", rule.module)
                    true
                }
            }
        }
    }

    /**
     * This method is in charge of adding the content of a dependency so that it contains all its information.
     */
    fun configDependency(dependenciesNode: Node, scope: String, addedDeps: ArrayList<Dependency>, dependency: Dependency) {
        if (shouldAddDependency(addedDeps, dependency)) {
            val dependencyNode = dependenciesNode.appendNode(DEPENDENCY_CONSTANT)

            addedDeps.add(dependency)

            addGroup(dependencyNode, dependency)
            addName(dependencyNode, dependency)
            addVersion(dependencyNode, dependency)
            addScope(dependencyNode, scope)
            addExclusions(dependencyNode, dependency as? ModuleDependency)
        }
    }

    /**
     * This method is in charge of finding all the dependencies of the project and to add them to the publication.
     */
    fun injectDependencies(project: Project, xmlProvider: XmlProvider, variantName: String, flavor: String?) {
        val dependenciesNode = xmlProvider.asNode().appendNode(DEPENDENCIES_CONSTANT)

        val addedDeps = ArrayList<Dependency>()

        project.configurations.all {
            val scope = scope(name, variantName, flavor)
            if (scope != null) {
                for (dependency in allDependencies) {
                    configDependency(dependenciesNode, scope, addedDeps, dependency)
                }
            }
        }
    }
}
