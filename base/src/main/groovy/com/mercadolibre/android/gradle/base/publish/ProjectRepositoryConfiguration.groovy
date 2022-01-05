package com.mercadolibre.android.gradle.base.publish

import org.gradle.api.Project

class ProjectRepositoryConfiguration {

    static void setupPublishingRepositories(Project project, List<Repository> repositories) {

        // This is a workaround for Gradle 5 that does not support PasswordCredentials.
        // Once all apps are on Gradle 6+, we can remove this all and leave only
        // the "repositories" block, using the method of described here:
        // https://docs.gradle.org/6.8.1/userguide/declaring_repositories.html#sec:handling_credentials
        def homePath = System.properties['user.home']
        def props = new Properties()
        File propertiesFile = new File(homePath + '/.gradle/gradle.properties')
        propertiesFile.withInputStream { props.load(it) }

        //Releases and Experimental credentials are the same, so we choose Releases
        def artifactsUser = props['AndroidInternalReleasesUsername']
        def artifactsPass = props['AndroidInternalReleasesPassword']

        repositories.forEach({ repository ->

            project.publishing.repositories.maven {
                name = repository.name
                url = repository.url
                // When everyone upgrades to Gradle 6+, we can leave this line and remove the workaround above
                //credentials(PasswordCredentials)
                credentials {
                    credentials {
                        username artifactsUser
                        password artifactsPass
                    }
                }
            }
        })
    }
}
