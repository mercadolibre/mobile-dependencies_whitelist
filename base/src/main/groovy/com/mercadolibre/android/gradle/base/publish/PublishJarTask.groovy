package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.PomUtils
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.XmlProvider
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel

/**
 * Created by saguilera on 7/23/17.
 */
abstract class PublishJarTask extends PublishTask {

    Project project

    def variant

    String taskName

    @Override
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
            /**
             * Translates "_" in flavor names to "-" for artifactIds, because "-" in flavor name is an
             * illegal character, but is well used in artifactId names.
             */
            def baseVariantArtifactId = variant.name.replace('_', '-')
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
            def javaDocDestDir = project.file("${project.buildDir}/docs/javadoc ${variantArtifactId}")

            /**
             * Includes
             */
            def sourceDirs = variant.allSource
            String taskNameJavaDoc = "${variant.name}Javadoc"
            TaskProvider<Javadoc> javadoc

            if (project.tasks.names.contains(taskNameJavaDoc)) {
                javadoc = project.tasks.named(taskNameJavaDoc)
            } else {
                javadoc = project.tasks.register(taskNameJavaDoc, Javadoc)
                javadoc.configure {
                    description "Generates Javadoc for ${variant.name}."
                    group 'Documentation'
                    source = sourceDirs
                    destinationDir = javaDocDestDir

                    if (JavaVersion.current().isJava8Compatible()) {
                        options.addStringOption('Xdoclint:none', '-quiet')
                    }

                    options.memberLevel = JavadocMemberLevel.PROTECTED

                    options.links("http://docs.oracle.com/javase/7/docs/api/")
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
                    description "Puts Javadoc for ${variant.name} in a jar."
                    group 'Documentation'
                    classifier = 'javadoc'
                    from javadoc.get().destinationDir
                    dependsOn javadoc
                }
            }

            String sourcesTaskName = "${variant.name}SourcesJar"
            TaskProvider<Task> sourcesJar

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
                artifacts = [project.tasks.jar, sourcesJar.get(), javadocJar.get()]

                pom.withXml { XmlProvider xmlProvider ->
                    xmlProvider.asNode().packaging*.value = 'jar'

                    PomUtils.injectDependencies(project, xmlProvider, variant.name)
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
}