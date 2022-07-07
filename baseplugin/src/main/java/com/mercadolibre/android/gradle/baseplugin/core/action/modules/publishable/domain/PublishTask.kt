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
 * This class generates the All posts with help of PomUtils
 */
abstract class PublishTask : ExtensionGetter() {

    val versionContainer = VersionContainer()
    lateinit var nameManager: PublishManager

    lateinit var project: Project
    lateinit var taskName: String

    fun getTimestamp(): String {
        return SimpleDateFormat(PUBLISHING_TIME_GENERATOR).apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date())
    }

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
