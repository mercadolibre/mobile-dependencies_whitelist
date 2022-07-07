package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.DOCUMENTATION_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGING_JAR_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_JAVADOC_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_SOURCES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCES_CONSTANT
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import java.io.File

/**
 * The PublishManager class is in charge of providing the variables and basic tasks so that a publication can be carried out.
 *
 * - SourceTask
 * - JavaDocTask
 */
class PublishManager(private val variantName: String, variantFlavor: String?, val project: Project, val sourceDirs: Any) {

    var javaDocDestDir: File

    var taskNameJavaDoc: String = ""
    var javadocJarTaskName: String = ""
    var sourcesTaskName: String = ""

    lateinit var javadoc: TaskProvider<*>
    lateinit var javadocJar: TaskProvider<*>
    var sourcesJar: TaskProvider<*>

    init {

        val baseVariantArtifactId = (if (!variantFlavor.isNullOrEmpty()) variantFlavor else variantName).replace("_", "-")

        javaDocDestDir = project.file("${project.buildDir}/docs/javadoc/${project.name}-$baseVariantArtifactId")

        taskNameJavaDoc = "${variantName}$PUBLISHING_JAVADOC_TASK"
        javadocJarTaskName = "${variantName}$PUBLISHING_JAVADOC_TASK$PACKAGING_JAR_CONSTANT"
        sourcesTaskName = "${variantName}$PUBLISHING_SOURCES_TASK$PACKAGING_JAR_CONSTANT"

        sourcesJar = if (project.tasks.names.contains(sourcesTaskName)) {
            project.tasks.named(sourcesTaskName)
        } else {
            project.tasks.register(sourcesTaskName, Jar::class.java).apply {
                configure {
                    description = "Puts sources for $variantName in a jar."
                    group = PACKAGING_GROUP
                    from(sourceDirs)
                    classifier = SOURCES_CONSTANT
                }
            }
        }
    }

    fun configJavaDocJar() {
        javadocJar = if (project.tasks.names.contains(javadocJarTaskName)) {
            project.tasks.named(javadocJarTaskName)
        } else {
            project.tasks.register(javadocJarTaskName, org.gradle.api.tasks.bundling.Jar::class.java).apply {
                configure {
                    description = "Puts Javadoc for $variantName in a jar."
                    group = DOCUMENTATION_GROUP
                    classifier = PUBLISHING_JAVADOC_TASK.toLowerCase()
                    from(javadoc.get().temporaryDir)
                    dependsOn(javadoc)
                }
            }
        }
    }
}
