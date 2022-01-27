package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.PomUtils
import com.mercadolibre.android.gradle.base.utils.VariantUtils
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel

/**
 * Created by saguilera on 7/23/17.
 */
abstract class PublishAarTask extends PublishTask {

    Project project

    def variant

    String taskName

    TaskProvider<Task> register(Builder builder) {
        project = builder.project
        variant = builder.variant
        taskName = builder.taskName

        if (variant == null || project == null || taskName == null) {
            throw new GradleException("Builder was missing properties")
        }
    }

    protected void createMavenPublication() {
        project.publishing.publications { PublicationContainer it ->
            def flavored = variant.flavorName && !variant.flavorName.isEmpty()

            /**
             * Translates "_" in flavor names to "-" for artifactIds, because "-" in flavor name is an
             * illegal character, but is well used in artifactId names.
             */
            def baseVariantArtifactId = flavored ? variant.flavorName.replace('_', '-') : variant.name
            def variantArtifactId = "${project.name}-$baseVariantArtifactId"

            /**
             * If the javadoc destinationDir wasn't changed per flavor, the libraryVariants would
             * overwrite the javaDoc as all variants would write in the same directory
             * before the last javadoc jar would have been built, which would cause the last javadoc
             * jar to include classes from other flavors that it doesn't include.
             *
             * Yes, tricky.
             *
             * Note that "${buildDir}/docs/javadoc" is the default javadoc destinationDir.
             */
            def javaDocDestDir = project.file("${project.buildDir}/docs/javadoc/${variantArtifactId}")

            /**
             * Includes
             */
            def sourceDirs = variant.sourceSets.collect {
                it.javaDirectories // Also includes kotlin sources if any.
            }

            String javadocTaskName = "${variant.name}Javadoc"
            TaskProvider<Javadoc> javadoc
            if (project.tasks.names.contains(javadocTaskName)) {
                javadoc = project.tasks.named(javadocTaskName)
            } else {
                javadoc = project.tasks.register(javadocTaskName, Javadoc)
                javadoc.configure {
                    description "Generates Javadoc for ${variant.name}."
                    group 'Documentation'

                    // Yes, javaCompile is deprecated, but whats the alternative?
                    source = VariantUtils.javaCompile(variant).source

                    destinationDir = javaDocDestDir

                    classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
                    project.configurations.findAll { it.canBeResolved && it.state != Configuration.State.UNRESOLVED }.each {
                        classpath += it
                    }

                    if (JavaVersion.current().isJava8Compatible()) {
                        options.addStringOption('Xdoclint:none', '-quiet')
                    }

                    options.memberLevel = JavadocMemberLevel.PROTECTED

                    options.links("http://docs.oracle.com/javase/7/docs/api/")
                    options.links("http://d.android.com/reference/")
                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                    failOnError false
                }
            }

            String javadocJarTaskName = "${variant.name}JavadocJar"
            TaskProvider<Jar> javadocJar
            if (project.tasks.names.contains(javadocJarTaskName)) {
                javadocJar = project.tasks.named(javadocJarTaskName)
            } else {
                javadocJar = project.tasks.register(javadocJarTaskName, Jar)
                javadocJar.configure {
                    dependsOn javadoc
                    description "Puts Javadoc for ${variant.name} in a jar."
                    group 'Documentation'
                    classifier = 'javadoc'
                    from javadoc.get().destinationDir
                }
            }

            String sourcesTaskName = "${variant.name}SourcesJar"
            TaskProvider<Jar> sourcesJar
            if (project.tasks.names.contains(sourcesTaskName)) {
                sourcesJar = project.tasks.named(sourcesTaskName)
            } else {
                sourcesJar = project.tasks.register(sourcesTaskName, Jar)
                sourcesJar.configure {
                    description "Puts sources for ${variant.name} in a jar."
                    group 'Packaging'

                    from sourceDirs
                    classifier = 'sources'
                }
            }

            it.register(taskName, MavenPublication).configure {
                artifactId = project.name
                groupId = project.group
                version = VersionContainer.get(project.name, taskName, project.version as String)

                artifacts = [
                        VariantUtils.packageLibrary(variant),
                        sourcesJar.get(),
                        javadocJar.get()
                ]

                pom.withXml { XmlProvider xmlProvider ->
                    xmlProvider.asNode().packaging*.value = 'aar'

                    PomUtils.injectDependencies(project, xmlProvider, variant.name, variant.flavorName)
                    PomUtils.composeDynamicDependencies(project, xmlProvider)

                    project.file("${project.buildDir}/publications/${taskName}/pom-default.xml")
                            .write(xmlProvider.asString().toString())
                }
            }

            String pomTaskName = "generatePomFileFor${taskName.capitalize()}Publication"
            if (project.tasks.names.contains(pomTaskName)) {
                project.tasks.named(taskName).configure {
                    dependsOn pomTaskName
                }
            }
        }
    }

    /**
     * Returns a flavored version if the variant is flavored, else the version you provided
     * @param version to flavorize
     * @param variant possibly flavored
     * @return flavored version or the same
     */
    protected static String flavorVersion(String version, def variant) {
        if (variant.flavorName) {
            return "${variant.flavorName}-${version}"
        }
        return version
    }

    /**
     * Returns for AGP 3.2.0 or higher bundle${variant.name.capitalize()}Aar
     * otherwise bundle${variant.name.capitalize()}
     * @param project
     * @param variant
     * @return bundle task name
     */
    protected static String getBundleTaskName(Project project, def variant) {
        def bundleTask = "bundle${variant.name.capitalize()}"
        return project.tasks.names.contains("${bundleTask}Aar") ? "${bundleTask}Aar" : bundleTask
    }

    /**
     * Returns source jar task name
     * @param variant
     * @return ${variant.name}SourcesJar string
     */
    protected static String getSourcesJarTaskName(def variant) {
        return "${variant.name}SourcesJar"
    }

    /**
     * Returns javadoc jar task name
     * @param variant
     * @return ${variant.name}JavadocJar string
     */
    protected static String getJavadocJarTask(def variant) {
        return "${variant.name}JavadocJar"
    }
}
