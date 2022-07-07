package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PublishManager
import com.mercadolibre.android.gradle.baseplugin.core.components.DOCUMENTATION_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_JAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_JAVADOC_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_JAVADOC_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_LINKS_JAR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_OPTIONS
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCES_CONSTANT
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.external.javadoc.StandardJavadocDocletOptions

/**
 * This class generates the Jar posts with help of TaskGenerator
 */
abstract class PublishJarTask : PublishTask() {

    lateinit var variant: SourceSet

    abstract fun register(project: Project, variant: SourceSet, taskName: String): TaskProvider<Task>

    fun getListOfDependsOn(): List<String> {
        return listOf(
            "${variant.name}$PUBLISHING_JAVADOC_TASK$PACKAGING_JAR_CONSTANT",
            PACKAGING_JAR_CONSTANT.toLowerCase(),
            "${variant.name}${SOURCES_CONSTANT.capitalized()}$PACKAGING_JAR_CONSTANT"
        )
    }

    fun createMavenPublication() {
        nameManager = PublishManager(variant.name, null, project, variant.allSource)

        with(nameManager) {
            javadoc =
                if (project.tasks.names.contains(taskNameJavaDoc)) {
                    project.tasks.named(taskNameJavaDoc)
                } else {
                    project.tasks.register(taskNameJavaDoc, Javadoc::class.java).apply {
                        configure {
                            description = "$PUBLISHING_JAVADOC_DESCRIPTION ${variant.name}."
                            group = DOCUMENTATION_GROUP
                            source = sourceDirs as FileTree

                            setDestinationDir(nameManager.javaDocDestDir)

                            if (JavaVersion.current().isJava8Compatible) {
                                for (commandLineOption in PUBLISHING_OPTIONS) {
                                    (options as StandardJavadocDocletOptions)
                                        .addStringOption(commandLineOption.key, commandLineOption.value)
                                }
                            }

                            options.memberLevel = JavadocMemberLevel.PROTECTED

                            for (jarLink in PUBLISHING_LINKS_JAR) {
                                (options as StandardJavadocDocletOptions).links(jarLink)
                            }
                            isFailOnError = false
                        }
                    }
                }

            configJavaDocJar()

            val artifacts = arrayListOf<Any>(
                sourcesJar.get(),
                javadocJar.get()
            )

            if (project.tasks.findByPath(PACKAGING_JAR_CONSTANT.toLowerCase()) != null) {
                artifacts.add(project.tasks.getByPath(PACKAGING_JAR_CONSTANT.toLowerCase()))
            }

            registerPublish(project, artifacts, variant.name, null)
        }
    }
}
