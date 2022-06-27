package com.mercadolibre.android.gradle.baseplugin.core.action.providers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_USER_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_USER_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Repository
import java.io.File
import java.util.Properties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

internal class RepositoryProvider {

    companion object {
        val INTERNAL_EXPERIMENTAL = "AndroidInternalExperimental"
        val INTERNAL_EXPERIMENTAL_URL = "https://android.artifacts.furycloud.io/repository/experimental/"
        val INTERNAL_RELEASES = "AndroidInternalReleases"
        val INTERNAL_RELEASES_URL = "https://android.artifacts.furycloud.io/repository/releases/"
        val EXTERNAL_RELEASES = "AndroidExternalReleases"
        val PUBLIC_RELEASES = "AndroidPublicReleases"
        val PUBLIC_AND_EXTERNAL_RELEASES_URL = "https://artifacts.mercadolibre.com/repository/android-releases/"
        val ANDROID_EXTRA = "AndroidExtra"
        val ANDROID_EXTRA_URL = "https://android.artifacts.furycloud.io/repository/extra/"

        val MERCADOLIBRE_PACKAGE = "com\\.mercadolibre\\..*"
        val MERCADOPAGO_PACKAGE = "com\\.mercadopago\\..*"
        val MERCADOENVIOS_PACKAGE = "com\\.mercadoenvios\\..*"

        val REGEX = ".*"
        val PUBLISH_REGEX = "^((?!EXPERIMENTAL-|LOCAL-).)*$"
        val PUBLISH_LOCAL_REGEX = "^(.*-)?LOCAL-.*$"
        val PUBLISH_EXPERIMENTAL_REGEX = "^(.*-)?EXPERIMENTAL-.*$"
    }

    private val REPOSITORIES = arrayListOf(
        Repository(PUBLIC_RELEASES, PUBLIC_AND_EXTERNAL_RELEASES_URL),
        Repository(INTERNAL_EXPERIMENTAL, INTERNAL_EXPERIMENTAL_URL),
        Repository(INTERNAL_RELEASES, INTERNAL_RELEASES_URL)
    )

    fun getRepositories(): ArrayList<Repository> {
        return REPOSITORIES
    }

    fun getRepositoriesDescription(): String {
        var description = ""
        for (repository in getRepositories()) {
            description += "\t\t- ${repository.name.ansi(ANSI_YELLOW)} --> ${repository.url.ansi(ANSI_GREEN)}\n"
        }
        return description
    }

    fun setupFetchingRepositories(project: Project) {
        val homePath = System.getProperty("user.home")
        val props = Properties()
        val propertiesFile = File("$homePath/.gradle/gradle.properties")
        props.load(propertiesFile.readText().reader())

        val artifactsUser = props[ANDROID_USER_NAME] as String
        val artifactsPass = props[ANDROID_USER_PASSWORD] as String

        configRepo(project, artifactsUser, artifactsPass)

        project.subprojects {
            configRepo(this, artifactsUser, artifactsPass)
        }
    }

    private fun configRepo(project: Project, artifactsUser: String, artifactsPass: String) {


        with(project) {
            repositories {
                maven {
                    name = INTERNAL_RELEASES
                    url = uri(INTERNAL_RELEASES_URL)
                    credentials {
                        username = artifactsUser
                        password = artifactsPass
                    }
                    content {
                        includeVersionByRegex(MERCADOLIBRE_PACKAGE, REGEX, PUBLISH_REGEX)
                        includeVersionByRegex(MERCADOPAGO_PACKAGE, REGEX, PUBLISH_REGEX)
                        includeVersionByRegex(MERCADOENVIOS_PACKAGE, REGEX, PUBLISH_REGEX)
                        includeGroup("com.bugsnag")
                    }
                }

                maven {
                    name = EXTERNAL_RELEASES
                    url = uri(PUBLIC_AND_EXTERNAL_RELEASES_URL)
                    content {
                        includeVersionByRegex(MERCADOLIBRE_PACKAGE, REGEX, PUBLISH_REGEX)
                    }
                }

                maven {
                    name = INTERNAL_EXPERIMENTAL
                    url = uri(INTERNAL_EXPERIMENTAL_URL)
                    credentials {
                        username = artifactsUser
                        password = artifactsPass
                    }
                    content {
                        includeVersionByRegex(MERCADOLIBRE_PACKAGE, REGEX, PUBLISH_EXPERIMENTAL_REGEX)
                        includeVersionByRegex(MERCADOPAGO_PACKAGE, REGEX, PUBLISH_EXPERIMENTAL_REGEX)
                        includeVersionByRegex(MERCADOENVIOS_PACKAGE, REGEX, PUBLISH_EXPERIMENTAL_REGEX)
                    }
                }

                mavenLocal {
                    content {
                        includeVersionByRegex(MERCADOLIBRE_PACKAGE, REGEX, PUBLISH_LOCAL_REGEX)
                        includeVersionByRegex(MERCADOPAGO_PACKAGE, REGEX, PUBLISH_LOCAL_REGEX)
                        includeVersionByRegex(MERCADOENVIOS_PACKAGE, REGEX, PUBLISH_LOCAL_REGEX)
                    }
                }

                maven {
                    name = ANDROID_EXTRA
                    url = uri(ANDROID_EXTRA_URL)

                    credentials {
                        username = artifactsUser
                        password = artifactsPass
                    }
                }
            }
        }
    }
}