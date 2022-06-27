package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.publishable

import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Repository
import java.io.File
import java.net.URI
import java.util.Properties
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven

internal class ProjectRepositoryConfiguration: ExtensionGetter() {
    fun setupPublishingRepositories(project: Project, repositoriesList: List<Repository>) {
        val homePath = System.getProperties()["user.home"]
        val props = Properties()
        val propertiesFile = File("$homePath/.gradle/gradle.properties")
        props.load(propertiesFile.inputStream())

        val artifactsUser = props["AndroidInternalReleasesUsername"] as String
        val artifactsPass = props["AndroidInternalReleasesPassword"] as String

        findExtension<PublishingExtension>(project)?.apply {
            publications {
                repositories {
                    repositoriesList.all {
                        maven(MavenPublication::class.java){
                            name = it.name
                            url = URI.create(it.url)

                            credentials{
                                username = artifactsUser
                                password = artifactsPass
                            }
                        }
                        true
                    }
                }
            }
        }
    }
}