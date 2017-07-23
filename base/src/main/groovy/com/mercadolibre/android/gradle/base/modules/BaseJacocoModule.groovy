package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Created by saguilera on 7/22/17.
 */
abstract class BaseJacocoModule extends Module {

    def static final String JACOCO_VERSION = "0.7.7.201606060606"

    protected Project project

    @Override
    void configure(Project project) {
        this.project = project

        project.with {
            apply plugin: 'jacoco'

            jacoco {
                toolVersion = JACOCO_VERSION
            }

            tasks.withType(Test) {
                testLogging {
                    events "FAILED"
                    exceptionFormat "full"
                }
            }
        }

        createCleanJacocoTask()
        createJacocoFinalProjectTask()
    }

    /**
     * Create Full Jacoco Report including all subprojects.
     */
    public createJacocoFinalProjectTask() {
        JacocoReport fullReportTask = project.tasks.create("jacocoFullReport", JacocoReport)
        fullReportTask.configure {
            reports.xml.enabled = true
            reports.html.enabled = true

            setOnlyIf {
                executionData.files.any { it.exists() }
            }

            def reportTasks = getReportTasks(project)

            doFirst {
                executionData = project.files(executionData.findAll { it.exists() }.flatten())
                project.logger.info("Setting up jacocoFullReport for: " + reportTasks)
            }

            /**
             * Filter for nulls since some JacocoReport tasks may have no classDirectories or sourceDirectories
             * configured, for example if there are no tests for a subproject.
             */
            executionData project.files({ reportTasks.executionData })
            classDirectories = project.files({
                reportTasks.collect { it.classDirectories }.findAll {
                    it != null
                }
            })
            sourceDirectories = project.files({
                reportTasks.collect { it.sourceDirectories }.findAll {
                    it != null
                }
            })
        }
    }

    /**
     * Find list of projects whose Jacoco report tasks are to be considered.
     */
    def static getReportTasks(Project project) {
        project.with {
            if (hasProperty('variant')) {
                return collectJacocoTasks(subprojects, getProperty('variant') as String)
            } else {
                return collectJacocoTasks(subprojects)
            }
        }
    }

    /**
     * Find all JacocoReport tasks except for the jacocoFullReport task we're creating here.
     */
    def static collectJacocoTasks(Set<Project> projects, String variant = 'Release') {
        return projects.collect {
            it.tasks.withType(JacocoReport).findAll {
                it.name.endsWith(variant)
            }
        }.flatten()
    }

    def void createCleanJacocoTask() {
        def task = project.tasks.create 'cleanJacocoFiles'
        task.setDescription('Clean all Jacoco related files.')

        task.doLast {
            File file = project.file("./jacoco.exec")
            if (!file.delete()) {
                throw new GradleException("Cannot delete \"jacoco.exec\" file. Check if some process is using it and close it.")
            }
        }

        task.onlyIf {
            File file = project.file("./jacoco.exec")
            return file.exists()
        }

        task.getOutputs().upToDateWhen {
            File file = project.file("./jacoco.exec")
            return !file.exists()
        }

        def cleanTask = project.tasks.findByName("clean")
        cleanTask.finalizedBy task
    }

}
