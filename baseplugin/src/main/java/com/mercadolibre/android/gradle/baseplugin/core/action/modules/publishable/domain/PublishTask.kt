package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PublishManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.VersionContainer
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.dto.*
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

/**
 * This class generates the All posts with help of PomUtils.
 */
abstract class PublishTask : ExtensionGetter() {

    /** This variable contains the object that will manage the release versions. */
    val versionContainer = VersionContainer()

    /** This variable contains the object that will manage the names of the tasks and generates some of them. */
    lateinit var nameManager: PublishManager

    /** This variable contains the project where the publication task is generated. */
    lateinit var project: Project

    /** This variable contains the name of the task that is being generated. */
    lateinit var taskName: String

    /**
     * This method is in charge of registering the publication so that it is accessible from other repositories.
     */
    fun registerPublish(project: Project, artifacts: List<Any?>, variantName: String, variantFlavor: String?) {
        findExtension<PublishingExtension>(project)?.apply {
            publications {
                register(taskName, MavenPublication::class.java).configure {
                    artifactId = project.name
                    groupId = project.group as String
                    version = versionContainer.get(project.name, taskName, project.version as String)
                    setArtifacts(artifacts)
                    PublishableFactory.getPublishable(
                        project,
                        taskName,
                        this,
                        variantName,
                        variantFlavor
                    ).process()
                }
            }
        }
    }

}
