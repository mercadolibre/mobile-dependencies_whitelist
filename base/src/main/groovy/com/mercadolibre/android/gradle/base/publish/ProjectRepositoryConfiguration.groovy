package com.mercadolibre.android.gradle.base.publish

import org.gradle.api.GradleException
import org.gradle.api.Project

class ProjectRepositoryConfiguration {
    private static final String REPOSITORY_CREDENTIALS_NOT_FOUND_MESSAGE = "Repositories credentials not found"

    static void setupPublishingRepositories(Project project, List<Repository> repositories) {
        repositories.forEach({ repository ->
            if (repository.credentials.username == null || repository.credentials.password == null) {
                println "[!] Missing repository credentials. These are required to publish artifacts"
                throw new GradleException(REPOSITORY_CREDENTIALS_NOT_FOUND_MESSAGE)
            }

            project.publishing.repositories.maven {
                name = repository.name
                url = repository.url
                credentials {
                    credentials {
                        username repository.credentials.username
                        password repository.credentials.password
                    }
                }
            }
        })
    }
}
