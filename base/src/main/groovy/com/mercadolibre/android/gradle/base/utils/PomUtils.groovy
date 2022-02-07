package com.mercadolibre.android.gradle.base.utils

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleVersionIdentifier

/**
 * Created by saguilera on 7/21/17.
 */
final class PomUtils {

    private static final String DEPENDENCY_LOCK_FOLDER_NAME = "gradle/dependency-locks"

    private static String scope(String configuration, String variantName, String flavor) {
        // Here I declare all the configurations we support to upload to the pom.
        // We also take into account flavored ones
        List<String> compileConfigurations = ['default', 'archives', 'compile', "${variantName}Compile".toString(),
                                              'implementation', "${variantName}Implementation".toString(),
                                              'api', "${variantName}Api".toString()]
        if (flavor) {
            compileConfigurations << "${flavor}Api".toString()
            compileConfigurations << "${flavor}Implementation".toString()
            compileConfigurations << "${flavor}Compile".toString()
        }

        final String[] testConfigurations = ['test', "test${variantName.capitalize()}", 'testCompile',
                                             "testCompile${variantName.capitalize()}"]
        final String[] runtimeConfigurations = ['runtime', 'runtimeOnly', "${variantName}Runtime",
                                                "${variantName}RuntimeOnly"]
        final String[] providedConfigurations = ['provided', 'compileOnly', "${variantName}CompileOnly"]

        switch (configuration) {
            case providedConfigurations:
                return 'provided'
            case runtimeConfigurations:
                return 'runtime'
            case testConfigurations:
                return 'test'
            case compileConfigurations:
                return 'compile'
            default:
                return null
        }
    }

    private static boolean shouldAddDependency(List<Dependency> deps, Dependency dep) {
        boolean isWellFormed = dep.group && dep.name && dep.version

        if (!isWellFormed) {
            return false
        }

        return deps.find { it.group == dep.group && it.name == dep.name && it.version == dep.version } == null
    }

    private static void addGroup(Node node, Dependency dependency) {
        node.appendNode('groupId', dependency.group)
    }

    private static void addName(Node node, Dependency dependency) {
        node.appendNode('artifactId', dependency.name)
    }

    private static void addVersion(Node node, Dependency dependency, XmlProvider xmlProvider, Project project) {
        // If it is a local dependency
        if (artifactIsFromProject(project.rootProject, dependency)) {
            // Its a local dependency, so lets further check
            // if maybe the version has a timestamp, in which
            // case, we should convert it to a dynamic version.
            if (xmlProvider.asNode().version.text() ==~ /^.*-\d{10,16}/) {
                // The version has at the end a timestamp.
                // We will add the version as dynamic
                // If the user doesnt want this, he should before publishing
                // a module, publish its dependants and change
                // the local compilation
                node.appendNode('version', dependency.version.replaceAll(/\..*/, "\\.+"))
            } else {
                // If it doesnt have a timestamp, we should assume its a
                // production publication, where the user explicitly
                // wants this version (its already published or will be
                // soon
                node.appendNode('version', dependency.version)
            }
        } else {
            // This dependency isnt local, so use the verison it has.
            node.appendNode('version', dependency.version)
        }
    }

    private static void addScope(Node node, String scope) {
        node.appendNode('scope', scope)
    }

    private static void addExclusions(Node node, Dependency dependency) {
        // Add the exclusions of the dependency
        if (!dependency.excludeRules.isEmpty()) {
            Node exclusionsNode = node.appendNode('exclusions')
            dependency.excludeRules.each { rule ->
                Node exclusionNode = exclusionsNode.appendNode('exclusion')
                exclusionNode.appendNode('groupId', rule.group ?: '*')
                exclusionNode.appendNode('artifactId', rule.module ?: '*')
            }
        }
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    static void injectDependencies(Project project, XmlProvider xmlProvider, String variantName = 'release', String flavor = '') {
        // Since maven-publish has a bug in the current version because it resolves lazily
        // we have to add the dependencies barehanded
        // https://discuss.gradle.org/t/maven-publish-doesnt-include-dependencies-in-the-project-pom-file/8544
        // Its also in the bintray docs if you are interested
        def dependenciesNode = xmlProvider.asNode().appendNode('dependencies')

        List<Dependency> addedDeps = new ArrayList<>()

        project.configurations.all { Configuration configuration ->
            def scope = scope(configuration.name, variantName, flavor)
            if (scope) {
                configuration.allDependencies.each { Dependency dependency ->
                    // Check they all exists. For example if using
                    // compile localGroovy() -> this is ":unspecified:"
                    if (shouldAddDependency(addedDeps, dependency)) {
                        addedDeps.add(dependency)

                        Node dependencyNode = dependenciesNode.appendNode('dependency')
                        addGroup(dependencyNode, dependency)
                        addName(dependencyNode, dependency)
                        addVersion(dependencyNode, dependency, xmlProvider, project)
                        addScope(dependencyNode, scope)
                        addExclusions(dependencyNode, dependency)
                    }
                }
            }
        }
    }

    static boolean artifactIsFromProject(Project project, Dependency dependency) {
        return project.subprojects.find { it.name == dependency.name && it.group == dependency.group } != null
    }

    static void composeDynamicDependencies(Project project, XmlProvider xmlProvider) {
        // This is another compose where we change dynamic versions by the ones declared in the .lock (if existent)
        // If the dependency lock file exists then change + for locked.
        // Else its just a dynamic dependency declared by someone who wants it like that
        if (project.file(DEPENDENCY_LOCK_FOLDER_NAME).exists()) {
            //For now they are all release, so check in release and compile. If in a future
            //we have also for debug, change here to find the release or debug accordingly
            xmlProvider.asNode().dependencies.'*'.findAll() {
                it.version.text().contains('+')
            }.each { def dependency ->
                // Look in the json if the dependency name exists
                project.file(DEPENDENCY_LOCK_FOLDER_NAME).eachFile {
                    def module = "${dependency.groupId[0]*.value[0]}:${dependency.artifactId[0]*.value[0]}"
                    it.eachLine { line ->
                        if (dependency.version[0]*.value[0].contains('+') && line.startsWith(module)) {
                            dependency.version*.value = line.split(':')[2]
                        }
                    }
                }
            }
        }
    }

}
