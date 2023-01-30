package com.mercadolibre.android.gradle.baseplugin.dto

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class DefaultPublishable(
    project: Project,
    taskName: String,
    private val mavenPublication: MavenPublication,
    private val variantName: String,
    private val variantFlavor: String?
) : AbstractPublishable(project, taskName) {

    override fun process() {
        configurePom(mavenPublication.pom, variantName, variantFlavor)
    }

}
