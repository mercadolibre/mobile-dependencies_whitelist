package com.mercadolibre.android.gradle.base.utils

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration

/**
 * Created by saguilera on 7/21/17.
 */
final class PomUtils {

    private static final String DEPENDENCY_LOCK_FILE_NAME = "dependencies.lock"

    @SuppressWarnings("GroovyAssignabilityCheck")
    static void injectDependencies(Project project, XmlProvider xmlProvider, String variantName = 'release') {
        // Since maven-publish has a bug in the current version because it resolves lazily
        // we have to add the dependencies barehanded
        // https://discuss.gradle.org/t/maven-publish-doesnt-include-dependencies-in-the-project-pom-file/8544
        // Its also in the bintray docs if you are interested
        def dependenciesNode = xmlProvider.asNode().appendNode('dependencies')

        // Here I declare all the configurations we support to upload to the pom.
        // We also take into account flavored ones
        // This doesnt give support to gradle 4.0 yet with implementation and stuff
        final String[] compileConfigurations = ['compile', "${variantName}Compile"]
        final String[] testConfigurations = ['test', "test${variantName.capitalize()}"]
        final String[] runtimeConfigurations = ['runtime']
        final String[] providedConfigurations = ['provided', 'compileOnly', "${variantName}CompileOnly"]
        final String[] all = [compileConfigurations, providedConfigurations, testConfigurations,
                              runtimeConfigurations].flatten()
        project.configurations.all { Configuration configuration ->
            if (all.contains(configuration.name)) {
                configuration.allDependencies.each {
                    // Check they all exists. For example if using
                    // compile localGroovy() -> this is ":unspecified:"
                    if (it.group && it.name && it.version) {
                        def dependencyNode = dependenciesNode.appendNode('dependency')
                        dependencyNode.appendNode('groupId', it.group)
                        dependencyNode.appendNode('artifactId', it.name)

                        if (it.group == xmlProvider.asNode().groupId.text() && !it.version) {
                            // Its a local dependency, so lets further check
                            // if maybe the version has a timestamp, in which
                            // case, we should convert it to a dynamic version.
                            if (xmlProvider.asNode().version.text() ==~ /^.*-\d{10,16}/) {
                                // The version has at the end a timestamp.
                                // We will add the version as dynamic
                                // If the user doesnt want this, he should before publishing
                                // a module, publish its dependants and change
                                // the local compilation
                                dependencyNode.appendNode('version', xmlProvider.asNode().version.text()
                                        .replaceAll(/-\d{10,16}/, '-+'))
                            } else {
                                // If it doesnt have a timestamp, we should assume its a
                                // production publication, where the user explicitly
                                // wants this version (its already published or will be
                                // soon
                                dependencyNode.appendNode('version', it.version)
                            }
                        } else {
                            // This dependency isnt local, so use the verison it has.
                            dependencyNode.appendNode('version', it.version)
                        }

                        def scope
                        switch (configuration.name) {
                            case providedConfigurations:
                                scope = 'provided'
                                break
                            case runtimeConfigurations:
                                scope = 'runtime'
                                break
                            case testConfigurations:
                                scope = 'test'
                                break
                            default:
                                scope = 'compile'
                                break
                        }
                        dependencyNode.appendNode('scope', scope)
                    }
                }
            }
        }
    }

    static void composeDynamicDependencies(Project project, XmlProvider xmlProvider) {
        // This is another compose where we change dynamic versions by the ones declared in the .lock (if existent)
        // If the dependency lock file exists then change + for locked.
        // Else its just a dynamic dependency declared by someone who wants it like that
        if (project.file(DEPENDENCY_LOCK_FILE_NAME).exists()) {
            def json = new JsonSlurper().parse(project.file(DEPENDENCY_LOCK_FILE_NAME))
            //For now they are all release, so check in release and compile. If in a future
            //we have also for debug, change here to find the release or debug accordingly
            xmlProvider.asNode().dependencies.'*'.findAll() {
                it.version.text().contains('+')
            }.each { def dependency ->
                // Look in the json if the dependency name exists
                json.each { def config ->
                    def jsonDependency = config.value["${dependency.groupId.text()}:${dependency.artifactId.text()}"]
                    if (jsonDependency && jsonDependency.locked && !jsonDependency.locked.contains('+')) {
                        dependency.version*.value = jsonDependency.locked
                    }
                }
            }
        }
    }

}
