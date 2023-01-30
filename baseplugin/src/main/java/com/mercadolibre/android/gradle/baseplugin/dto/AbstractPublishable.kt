package com.mercadolibre.android.gradle.baseplugin.dto

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PomUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.POM_FILE_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLICATIONS_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLICATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_POM_FILE
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import java.io.File

abstract class AbstractPublishable(
    private val project: Project,
    private val taskName: String
) {

    abstract fun process()

    fun configurePom(
        pom: MavenPom,
        variantName: String,
        variantFlavor: String?
    ) {
        val pomFile = project.file("${project.buildDir}/$PUBLICATIONS_CONSTANT/$taskName/$PUBLISHING_POM_FILE")
        pom.withXml {
            useXmlProvider(PomUtils(), this, variantName, variantFlavor, pomFile)
        }

        val pomTaskName = "$POM_FILE_TASK${taskName.capitalize()}${PUBLICATION_CONSTANT.capitalize()}"
        configurePomTaskIfExist(pomTaskName)
    }

    fun configurePomTaskIfExist(pomTaskName: String) {
        if (project.tasks.names.contains(pomTaskName)) {
            project.tasks.named(taskName).configure {
                dependsOn(pomTaskName)
            }
        }
    }

    fun makeComponentIfExist(
        mavenPublication: MavenPublication,
        component: SoftwareComponent
    ) {
        mavenPublication.from(component)
    }

    fun useXmlProvider(pomUtils: PomUtils, xmlProvider: XmlProvider, variantName: String, variantFlavor: String?, pomFile: File) {
        pomUtils.injectDependencies(project, xmlProvider, variantName, variantFlavor)
        pomFile.writeText(xmlProvider.asString().toString())
    }
}
