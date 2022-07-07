package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PublishManager
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.BUNDLE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.DOCUMENTATION_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_AAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_JAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXCLUDES_ARR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_JAVADOC_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_JAVADOC_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LINKS_ARR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_OPTIONS
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCES_CONSTANT
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import java.io.File

/**
 * This class generates the Aar Release posts with help of TaskGenerator
 */
abstract class PublishAarTask : PublishTask() {

    lateinit var variant: BaseVariant

    abstract fun register(project: Project, variant: BaseVariant, taskName: String): TaskProvider<Task>

    internal fun createMavenPublication() {
        val sourceDirs: MutableCollection<File> = mutableListOf()
        variant.sourceSets.all {
            sourceDirs += it.javaDirectories
            true
        }

        nameManager = PublishManager(variant.name, variant.flavorName, project, sourceDirs)

        with(nameManager) {
            javadoc =
                if (project.tasks.names.contains(taskNameJavaDoc)) {
                    project.tasks.named(taskNameJavaDoc)
                } else {
                    project.tasks.register(taskNameJavaDoc, org.gradle.api.tasks.javadoc.Javadoc::class.java).apply {
                        configure {
                            description = "$PUBLISHING_JAVADOC_DESCRIPTION ${variant.name}."
                            group = DOCUMENTATION_GROUP
                            source = VariantUtils.javaCompile(variant).source

                            setDestinationDir(nameManager.javaDocDestDir)

                            classpath += project.files(listOf(findExtension<BaseExtension>(project)?.bootClasspath, File.pathSeparator))

                            project.configurations.all {
                                if (isCanBeResolved && state != org.gradle.api.artifacts.Configuration.State.UNRESOLVED) {
                                    classpath += this
                                }
                            }

                            if (JavaVersion.current().isJava8Compatible) {
                                for (publishOption in PUBLISHING_OPTIONS) {
                                    (options as StandardJavadocDocletOptions).addStringOption(publishOption.key, publishOption.value)
                                }
                            }

                            options.memberLevel = JavadocMemberLevel.PROTECTED

                            for (publishLink in PUBLISHING_LINKS_ARR) {
                                (options as StandardJavadocDocletOptions).links(publishLink)
                            }

                            for (publishExclude in PUBLISHING_EXCLUDES_ARR) {
                                exclude(publishExclude)
                            }

                            isFailOnError = false
                        }
                    }
                }

            configJavaDocJar()

            registerPublish(
                project,
                listOf(sourcesJar, javadocJar.get(), VariantUtils.packageLibrary(variant)),
                variant.name,
                variant.flavorName
            )
        }
    }

    fun flavorVersion(version: String, variant: BaseVariant): String {
        if (!variant.flavorName.isNullOrEmpty()) {
            return "${variant.flavorName}-$version"
        }
        return version
    }

    fun getBundleTaskName(project: Project, variant: BaseVariant): String {
        val bundleTask = "$BUNDLE_CONSTANT${variant.name.capitalize()}"
        return if (project.tasks.names.contains("${bundleTask}$PACKAGING_AAR_CONSTANT")) {
            "${bundleTask}$PACKAGING_AAR_CONSTANT"
        } else {
            bundleTask
        }
    }

    fun getSourcesJarTaskName(variant: BaseVariant): String {
        return "${variant.name}${SOURCES_CONSTANT.capitalized()}$PACKAGING_JAR_CONSTANT"
    }

    fun getJavadocJarTask(variant: BaseVariant): String {
        return "${variant.name}$PUBLISHING_JAVADOC_TASK$PACKAGING_JAR_CONSTANT"
    }
}
