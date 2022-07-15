package com.mercadolibre.android.gradle.baseplugin.core.action.providers

import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_EXTRA
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_EXTRA_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_USER_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_USER_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.EXTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_EXPERIMENTAL_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_RELEASES_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.MERCADOENVIOS_PACKAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.MERCADOLIBRE_PACKAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.MERCADOPAGO_PACKAGE
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLIC_AND_EXTERNAL_RELEASES_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLIC_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_EXPERIMENTAL_REGEX
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_LOCAL_REGEX
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_REGEX
import com.mercadolibre.android.gradle.baseplugin.core.components.REGEX
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Repository
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import java.io.File
import java.util.Properties

/**
 * RepositoryProvider is in charge of providing all the Meli repositories where to bring and publish the dependencies.
 */
internal class RepositoryProvider {

    private val repositories = arrayListOf(
        Repository(PUBLIC_RELEASES, PUBLIC_AND_EXTERNAL_RELEASES_URL),
        Repository(INTERNAL_EXPERIMENTAL, INTERNAL_EXPERIMENTAL_URL),
        Repository(INTERNAL_RELEASES, INTERNAL_RELEASES_URL)
    )

    /**
     * This method is in charge of returning the repositories.
     */
    fun getRepositories(): ArrayList<Repository> = repositories

    /**
     * This method is responsible for returning a description of the modules that are implemented.
     */
    fun getRepositoriesDescription(): String {
        var description = ""
        for (repository in getRepositories()) {
            description += "- ${repository.name.ansi(ANSI_YELLOW)} $ARROW ${repository.url.ansi(ANSI_GREEN)}\n"
        }
        return description
    }

    /**
     * This method is in charge of configuring the credentials to have access to the added repositories.
     */
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
