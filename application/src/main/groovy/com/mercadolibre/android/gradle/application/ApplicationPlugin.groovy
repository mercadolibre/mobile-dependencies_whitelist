package com.mercadolibre.android.gradle.application

import org.gradle.api.Plugin
import org.gradle.api.Project

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

import java.io.File


/**
 * Created by ngiagnoni on 3/11/15.
 */
public class ApplicationPlugin implements Plugin<Project> {

    def project

    @Override
    public void apply(Project project) {
        this.project = project

        project.apply plugin: 'com.android.application'

        project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
        project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'

        project.apply plugin: 'nebula.dependency-lock'
    
        createTasks()
    }

    def createTasks() {
        hookLocksToAssembleWithSplit()
    }

    def hookLocksToAssembleWithSplit() {
        def task = project.tasks.create "lockVersions"
        task.setDescription('Locks the compiled project with the current versions of its dependencies to keep using them in future assembles')
        task.doLast {
            //Check and run this only if -PsplitApk
            println ":${project.name}:generateLock"
            project.generateLock.execute()
            println ":${project.name}:saveLock"
            project.saveLock.execute()

            def file = project.file('dependencies.lock')
            def inputJson = new JsonSlurper().parseText(file.text)
            inputJson.each { variant, variantJson ->
                if (!variant.contains("test") && !variant.contains("Test")) {
                    variantJson.each { dependency, dependencyVersions ->
                        if (dependencyVersions.locked.contains("ALPHA")) {
                            dependencyVersions.locked = dependencyVersions.locked.find(/.*\..*\.[0-9]+/) // Accepts [everything].[everything].[only numbers]
                        }
                        if (dependencyVersions.locked.contains("3.6.0")) {
                            dependencyVersions.locked = "3.3.2"
                            dependencyVersions.requested = "3.3.2"
                        }
                    }
                }
            }
            
            def jsonBuilder = new JsonBuilder(inputJson)
            file.withWriter {
                it.write jsonBuilder.toPrettyString()
            }
        }

        if (project.hasProperty('lock') || true) {
            project.preBuild.dependsOn "lockVersions" //Make before even building to generate the locks
            project.tasks.whenTaskAdded { addedTask -> //Remove the lock after assembleRelease
                if (addedTask.name == "assembleRelease") {
                    addedTask.doLast {
                        println ":${project.name}:deleteLock"
                        project.deleteLock.execute()
                    }
                }
            }
        }
    }

}
