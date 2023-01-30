package com.mercadolibre.android.gradle.baseplugin.dto

import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication

object PublishableFactory {

    fun getPublishable(
        project: Project,
        taskName: String,
        mavenPublication: MavenPublication,
        variantName: String,
        variantFlavor: String?,
        versionCatalogComponent: SoftwareComponent? = getVersionCatalogComponent(project),
        javaPlatformComponent: SoftwareComponent? = getJavaPlatformComponent(project)
    ): AbstractPublishable {
        return when {
            isJavaPlatformComponentNotNull(javaPlatformComponent) -> {
                JavaPlatformComponentPublishable(project, taskName, mavenPublication,
                    javaPlatformComponent!!)
            }
            isVersionCatalogComponentNotNull(versionCatalogComponent) -> {
                VersionCatalogComponentPublishable(project, taskName, mavenPublication,
                    variantName, variantFlavor, versionCatalogComponent!!)
            }
            else -> {
                DefaultPublishable(project, taskName, mavenPublication, variantName, variantFlavor)
            }
        }
    }

    fun getJavaPlatformComponent(project: Project): SoftwareComponent? {
        return project.components.findByName("javaPlatform")
    }

    fun isJavaPlatformComponentNotNull(platformComponent: SoftwareComponent?): Boolean {
        return platformComponent != null
    }

    fun getVersionCatalogComponent(project: Project): SoftwareComponent? {
        return project.components.findByName("versionCatalog")
    }

    fun isVersionCatalogComponentNotNull(versionCatalogComponent: SoftwareComponent?): Boolean {
        return versionCatalogComponent != null
    }

}
