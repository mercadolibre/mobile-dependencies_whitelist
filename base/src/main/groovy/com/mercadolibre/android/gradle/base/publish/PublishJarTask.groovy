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
abstract class PublishJarTask extends PublishTask {

    Project project

    def variant

    String taskName

    Task create(Builder builder) {
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
            /**
             * Translates "_" in flavor names to "-" for artifactIds, because "-" in flavor name is an
             * illegal character, but is well used in artifactId names.
             */
            def baseVariantArtifactId = variant.name.replace('_', '-')
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
            def javaDocDestDir = project.file("${project.buildDir}/docs/javadoc ${variantArtifactId}")

            /**
             * Includes
             */
            def sourceDirs = variant.allSource
            def javadoc = project.tasks.findByName("${variant.name}Javadoc")
            if (!javadoc) {
                javadoc = project.task("${variant.name}Javadoc", type: Javadoc) {
                    description "Generates Javadoc for ${variant.name}."
                    source = sourceDirs
                    destinationDir = javaDocDestDir
                    options.links("http://docs.oracle.com/javase/7/docs/api/");
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
