package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.utils.PomUtils
import com.mercadolibre.android.gradle.base.utils.VersionContainer
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPublication
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

    Task create(PublishTask.Builder builder) {
        project = builder.project
        variant = builder.variant
        taskName = builder.taskName

        if (variant == null || project == null || taskName == null) {
            throw new GradleException("Builder was missing properties")
        }
    }

    protected void createMavenPublication() {
        project.publishing.publications {
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
            def javadoc = project.tasks.findByName("${variant.name}Javadoc")
            if (!javadoc) {
                javadoc = project.task("${variant.name}Javadoc", type: Javadoc) {
                    description "Generates Javadoc for ${variant.name}."
                    group 'Documentation'

                    // Yes, javaCompile is deprecated, but whats the alternative?
                    source = variant.javaCompile.source
                    
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
            def javadocJar = project.tasks.findByName("${variant.name}JavadocJar")
            if (!javadocJar) {
                javadocJar = project.task("${variant.name}JavadocJar", type: Jar, dependsOn: javadoc) {
                    description "Puts Javadoc for ${variant.name} in a jar."
                    group 'Documentation'
                    classifier = 'javadoc'
                    from javadoc.destinationDir
                }
            }
            def sourcesJar = project.tasks.findByName("${variant.name}SourcesJar")
            if (!sourcesJar) {
                sourcesJar = project.task("${variant.name}SourcesJar", type: Jar) {
                    description "Puts sources for ${variant.name} in a jar."
                    group 'Packaging'

                    from sourceDirs
                    classifier = 'sources'
                }
            }

            "$taskName"(MavenPublication) {
                artifactId = project.name
                groupId = project.group
                version = VersionContainer.get(taskName, project.version as String)

                artifacts = [
                        "$project.buildDir/outputs/aar/${project.getName()}-${variant.name}.aar",
                        sourcesJar,
                        javadocJar
                ]

                pom.withXml { XmlProvider xmlProvider ->
                    xmlProvider.asNode().packaging*.value = 'aar'

                    PomUtils.injectDependencies(project, xmlProvider, variant.name)
                    PomUtils.composeDynamicDependencies(project, xmlProvider)

                    project.file("${project.buildDir}/publications/${taskName}/pom-default.xml")
                        .write(xmlProvider.asString().toString())
                }
            }

            project.tasks.whenTaskAdded {
                if (it.name.contains('generatePomFileFor')) {
                    String hookedTask = it.name.replaceFirst('generatePomFileFor', '').replaceFirst("Publication", '')

                    if (hookedTask != null && hookedTask.length() != 0) {
                        hookedTask = (Character.toLowerCase(hookedTask.charAt(0)) as String) + hookedTask.substring(1)
                        project.tasks.findByName(hookedTask).dependsOn it
                    }
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
    protected String flavorVersion(String version, def variant) {
        if (variant.flavorName) {
            return "${variant.flavorName}-${version}"
        }
        return version
    }

}
