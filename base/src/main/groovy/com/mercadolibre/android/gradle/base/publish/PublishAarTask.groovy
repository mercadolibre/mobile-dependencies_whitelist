package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.PomUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Created by saguilera on 7/23/17.
 */
abstract class PublishAarTask extends PublishTask {

    Project project

    def variant

    String taskName

    Task create(PublishTask.Builder builder) {
        project = builder.project
        variant = builder.variant
        taskName = builder.taskName

        if (variant == null || project == null || taskName == null) {
            throw new GradleException("Builder was missing properties")
        }
    }

    protected abstract String version()

    protected void createMavenPublication() {
        project.publishing.publications {
            def flavored = variant.flavorName && !variant.flavorName.isEmpty()

            /**
             * Translates "_" in flavor names to "-" for artifactIds, because "-" in flavor name is an
             * illegal character, but is well used in artifactId names.
             */
            def baseVariantArtifactId = flavored ? variant.flavorName.replace('_', '-') : variant.name
            def variantArtifactId = "${project.publisher.artifactId}-$baseVariantArtifactId"

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
            def javaDocDestDir = project.file("${project.buildDir}/docs/javadoc ${flavored ? variantArtifactId : ""}")

            /**
             * Includes
             */
            def sourceDirs = variant.sourceSets.collect {
                it.javaDirectories // Also includes kotlin sources if any.
            }
            def javadoc = project.tasks.findByName("${variant.name}Javadoc")
            if (!javadoc) {
                javadoc = project.task("${variant.name}Javadoc", type: Javadoc) {
                    description "Generates Javadoc for ${variant.name}."
                    source = variant.javaCompile.source // Yes, javaCompile is deprecated, but whats the alternative?
                    destinationDir = javaDocDestDir
                    classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
                    classpath += project.files(project.configurations.compile)
                    options.links("http://docs.oracle.com/javase/7/docs/api/");
                    options.links("http://d.android.com/reference/");
                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                    failOnError false
                }
            }
            def javadocJar = project.tasks.findByName("${variant.name}JavadocJar")
            if (!javadocJar) {
                javadocJar = project.task("${variant.name}JavadocJar", type: Jar, dependsOn: javadoc) {
                    description "Puts Javadoc for ${variant.name} in a jar."
                    classifier = 'javadoc'
                    from javadoc.destinationDir
                }
            }
            def sourcesJar = project.tasks.findByName("${variant.name}SourcesJar")
            if (!sourcesJar) {
                sourcesJar = project.task("${variant.name}SourcesJar", type: Jar) {
                    description "Puts sources for ${variant.name} in a jar."
                    from sourceDirs
                    classifier = 'sources'
                }
            }

            "$taskName"(MavenPublication) {
                artifactId project.publisher.artifactId
                groupId project.publisher.groupId
                version version()

                artifact variant.outputs[0].packageLibrary // This is the aar library
                artifact sourcesJar
                artifact javadocJar

                pom.withXml { XmlProvider xmlProvider ->
                    PomUtils.composeLocalDependencies(project, xmlProvider)
                }
                pom.withXml { XmlProvider xmlProvider ->
                    PomUtils.composeDynamicDependencies(project, xmlProvider)
                }
            }
        }
    }

}
