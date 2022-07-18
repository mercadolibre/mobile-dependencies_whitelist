package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.domain

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PomUtils
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.PublishManager
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.VersionContainer
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.POM_FILE_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLICATIONS_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLICATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_POM_FILE
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_GENERATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_ZONE
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.configurationcache.extensions.capitalized
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

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
     * This method is responsible for generating the timestamp so that the version does not have duplicates.
     */
    fun getTimestamp(): String =
        SimpleDateFormat(PUBLISHING_TIME_GENERATOR).apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date())

    /**
     * This method is in charge of registering the publication so that it is accessible from other repositories.
     */
    fun registerPublish(project: Project, artifacts: List<Any?>, variantName: String, variantFlavor: String?) {
        val pomUtils = PomUtils()

        findExtension<PublishingExtension>(project)?.apply {
            publications {
                register(taskName, MavenPublication::class.java).configure {
                    artifactId = project.name
                    groupId = project.group as String
                    version = versionContainer.get(project.name, taskName, project.version as String)

                    setArtifacts(artifacts)

                    pom.withXml {

                        pomUtils.injectDependencies(project, this, variantName, variantFlavor)

                        project.file("${project.buildDir}/$PUBLICATIONS_CONSTANT/$taskName/$PUBLISHING_POM_FILE")
                            .writeText(this.asString().toString())
                    }

                    val pomTaskName = "$POM_FILE_TASK${taskName.capitalize()}${PUBLICATION_CONSTANT.capitalized()}"
                    if (project.tasks.names.contains(pomTaskName)) {
                        project.tasks.named(taskName).configure {
                            dependsOn(pomTaskName)
                        }
                    }
                }
            }
        }
    }
}
