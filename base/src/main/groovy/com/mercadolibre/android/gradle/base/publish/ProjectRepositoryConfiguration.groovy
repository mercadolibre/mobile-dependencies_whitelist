package com.mercadolibre.android.gradle.base.publish

import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials

class ProjectRepositoryConfiguration {

    static void setupPublishingRepositories(Project project, List<Repository> repositories) {
        repositories.forEach({ repository ->

            project.publishing.repositories.maven {
                name = repository.name
                url = repository.url
                credentials(PasswordCredentials)
            }
        })
    }
}
