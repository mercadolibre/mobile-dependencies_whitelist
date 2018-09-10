package com.mercadolibre.android.gradle.base.utils

import org.gradle.api.Project

/**
 * Created by saguilera on 7/23/17.
 */
final class BintrayConfiguration {

    private static final String BINTRAY_USER_ENV = "BINTRAY_USER"
    private static final String BINTRAY_KEY_ENV = "BINTRAY_KEY"
    private static final String BINTRAY_PROP_FILE = "bintray.properties"
    private static final String BINTRAY_USER_PROP = "bintray.user"
    private static final String BINTRAY_KEY_PROP = "bintray.key"

    static void setBintrayConfig(Builder builder) {
        Project project = builder.project
        String publicationName = builder.publicationName
        String bintrayRepository = builder.bintrayRepository
        String publicationType = builder.publicationType
        String publicationPackaging = builder.publicationPackaging

        def finalPublications
        if (project.hasProperty('android')) {
            finalPublications = project.android?.productFlavors?.collect {
                "publish${publicationPackaging.capitalize()}Release${it.name.capitalize()}${publicationType.capitalize()}"
            } ?: [publicationName]
        } else {
            finalPublications = [publicationName]
        }

        project.tasks.bintrayUpload.with {
            repoName = bintrayRepository

            publications = finalPublications

            versionVcsTag = "${VersionContainer.get(project.name, publicationName, project.version as String)}"

            dryRun = false
            publish = true
            userOrg = 'mercadolibre'
            packageName = "${project.group}.${project.name}"
            versionName = "${VersionContainer.get(project.name, publicationName, project.version as String)}"

            packagePublicDownloadNumbers = false

            return it
        }

        loadBintrayCredentials(project)

        VersionContainer.logVersion("${project.group}:${project.name}:${VersionContainer.get(project.name, publicationName, project.version as String)}")
    }

    private static def loadBintrayCredentials(Project project) {
        File propsFile = project.file(BINTRAY_PROP_FILE)
        if (propsFile.exists()) {
            println "[!] Load Bintray credentials from '${BINTRAY_PROP_FILE}'. Please don't versioning this file."
            Properties props = new Properties()
            props.load(new FileInputStream(propsFile))
            project.bintrayUpload.user = props.getProperty(BINTRAY_USER_PROP)
            project.bintrayUpload.apiKey = props.getProperty(BINTRAY_KEY_PROP)
        } else if (System.getenv(BINTRAY_USER_ENV) && System.getenv(BINTRAY_KEY_ENV)) {
            project.bintrayUpload.user = System.getenv(BINTRAY_USER_ENV)
            project.bintrayUpload.apiKey = System.getenv(BINTRAY_KEY_ENV)
        } else {
            println "[!] Missing Bintray credentials"
            println "    You can set this values as enviroment variables: '${BINTRAY_USER_ENV}' and '${BINTRAY_KEY_ENV}'"
            println "    or into a property file ('${BINTRAY_PROP_FILE}'): '${BINTRAY_USER_PROP}' and '${BINTRAY_KEY_PROP}'"
        }
    }

    static class Builder {
        Project project
        String publicationName
        String bintrayRepository
        String publicationType
        String publicationPackaging
    }

}
