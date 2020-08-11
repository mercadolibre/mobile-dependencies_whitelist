package com.mercadolibre.android.gradle.base.modules


import org.gradle.api.Project
import org.gradle.api.initialization.Settings
/**
 * Build Scan module for creating a task that will publish build scans
 *
 * Created by mafunes on 24/04/19.
 */
class BuildScanModule implements Module, SettingsModule {

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
        configure(settings, settings.getRootDir().getName())
    }

    @Override
    void configure(Project project) {
        project.with {
            apply plugin:'com.gradle.build-scan'
        }
        configure(project, project.getProject().getName())
    }

}
