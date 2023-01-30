package com.mercadolibre.android.gradle.baseplugin.dto

import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication

class VersionCatalogComponentPublishable(
    project: Project,
    taskName: String,
    private val mavenPublication: MavenPublication,
    private val variantName: String,
    private val variantFlavor: String?,
    private val component: SoftwareComponent
) : AbstractPublishable(project, taskName) {

    override fun process() {
        makeComponentIfExist(mavenPublication, component)
        configurePom(mavenPublication.pom, variantName, variantFlavor)
    }

}
