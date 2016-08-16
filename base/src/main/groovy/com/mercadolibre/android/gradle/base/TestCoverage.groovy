package com.mercadolibre.android.gradle.base

import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport


/**
 * Created by cnieto on 10/8/16.
 */
class TestCoverage {

    def listSrc = []

    /**
     * Find list of projects whose Jacoco report tasks are to be considered.
     */
    static def getReportTasks(Project project, JacocoReport exclude) {

        def projects = project.allprojects

        /**
         * Find all JacocoReport tasks except for the jacocoFullReport task we're creating here.
         */
        if(project.hasProperty('variant')){
            def variant = project.getProperty('variant')
            def reportTasks = projects.collect {
                it.tasks.withType(JacocoReport).findAll {
                    it != exclude
                    it.name.endsWith(variant)
                }
            }.flatten()
            reportTasks
        }else{
            def reportTasks = projects.collect {
                it.tasks.withType(JacocoReport).findAll {
                    it != exclude
                    it.name.endsWith("Debug")
                }
            }.flatten()
            reportTasks
        }


    }

    /**
     * Create Full Jacoco Report including all subprojects.
     */
    public createJacocoFinalProjectTask(Project project) {
        project.apply plugin: 'jacoco'
        project.jacoco.toolVersion = "0.7.7.201606060606"
        JacocoReport fullReportTask = project.tasks.create("jacocoFullReport", JacocoReport)
        fullReportTask.configure {
            reports.xml.enabled = true
            reports.html.enabled = true

            /**
             * Implement fix mentioned in Gradle Source: https://github.com/gradle/gradle/blob/master/subprojects/jacoco/src/main/groovy/org/gradle/testing/jacoco/tasks/JacocoReport.groovy
             */
            setOnlyIf {
                executionData.files.any { it.exists() }
            }
            doFirst {
                executionData = project.files(executionData.findAll { it.exists() }.flatten())
                project.logger.info("Setting up jacocoFullReport for: " + getReportTasks(project, fullReportTask))
            }

            /** Filter for nulls since some JacocoReport tasks may have no classDirectories or sourceDirectories
             * configured, for example if there are no tests for a subproject.
             */
            executionData project.files({ getReportTasks(project, fullReportTask).executionData })
            classDirectories = project.files({
                getReportTasks(project, fullReportTask).collect { it.classDirectories }.findAll {
                    it != null
                }
            })
            sourceDirectories = project.files({
                getReportTasks(project, fullReportTask).collect { it.sourceDirectories }.findAll {
                    listSrc << it.getAsPath().toString()
                    it != null
                }
            })
        }
    }

    /**
     * Executes post to coveralls.io of the resulting .exec file
     */
    public createCoveragePost(project) {
        project.afterEvaluate{
            def task = project.getTasksByName("coveralls", false)
            task = task[0]
            if (task != null){
                task.description = "Post coverage report to coveralls.io"
                task.group = "Reporting"
                task.dependsOn "jacocoFullReport"
                project.findProperty("coveralls").jacocoReportPath = 'build/reports/jacoco/jacocoFullReport/jacocoFullReport.xml'
                project.findProperty("coveralls").sourceDirs = listSrc
                task.onlyIf({
                    System.env.'CI'
                })
            }
        }
    }
}