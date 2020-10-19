package com.mercadolibre.android.gradle.base.modules


import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.util.VersionNumber

/**
 * Build Scan module for creating a task that will publish build scans
 *
 * Created by mafunes on 24/04/19.
 */
class BuildScanModule implements Module, SettingsModule {

    private static final int GRADLE_VERSION_SIX = 6

    def configure(Object object, String projectName) {
        object.with {
            gradleEnterprise {
                buildScan {
                    publishAlways()
                    server = 'https://gradle.adminml.com/'
                    // tags
                    tag projectName
                    uploadInBackground = !System.getenv().containsKey("CI")

                    if (System.getenv().containsKey("CI")) {
                        tag 'CI'
                    } else {
                        tag 'Local'
                    }

                    // customs values
                    background {
                        def commitId = 'git rev-parse --verify HEAD'.execute().text.trim()
                        value "Git Commit ID", commitId
                        String branchName = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
                        value "Git branch", branchName
                        value "user_name", 'git config user.name'.execute().text.trim()
                        value "user_email", 'git config user.email'.execute().text.trim()
                        value "remote_url", 'git config --get remote.origin.url'.execute().text.trim()
                    }
                }
            }
        }
    }

    @Override
    void configure(final Settings settings) {
        def projectGradleVersion = VersionNumber.parse(settings.gradle.gradleVersion)
        if (projectGradleVersion.major >= GRADLE_VERSION_SIX) {
            settings.with {
                apply plugin : "com.gradle.enterprise"
            }
            configure(settings, settings.getRootProject().getName())
        }
    }

    @Override
    void configure(Project project) {
        def projectGradleVersion = VersionNumber.parse(project.gradle.gradleVersion)
        if (projectGradleVersion.major < GRADLE_VERSION_SIX) {
            project.with {
                apply plugin:'com.gradle.build-scan'
            }
            configure(project, project.getRootProject().getName())
        }
    }
}
