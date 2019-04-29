package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
/**
 * Build Scan module for creating a task that will publish build scans
 *
 * Created by mafunes on 24/04/19.
 */
class BuildScanModule implements Module {

    protected Project project

    @Override
    void configure(Project project) {
        this.project = project

        project.with {
            apply plugin:'com.gradle.build-scan'
            buildScan {
                server = 'https://gradle.adminml.com/'
                // tags
                tag project.getRootDir().getName()
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
