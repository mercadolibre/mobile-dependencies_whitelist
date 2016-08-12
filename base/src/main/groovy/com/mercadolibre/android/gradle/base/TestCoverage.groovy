package com.mercadolibre.android.gradle.base

import groovy.io.FileType
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree


/**
 * Created by cnieto on 10/8/16.
 */
class TestCoverage {

    def listClass = []
    def listSrc = []
    def testTaskName = ""

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
                    it.toString().endsWith(variant)
                }
            }.flatten()
            reportTasks
        }else{
            def reportTasks = projects.collect {
                it.tasks.withType(JacocoReport).findAll {
                    it != exclude
                    it.toString().endsWith("Debug")
                }
            }.flatten()
        }
    }

    public createJacocoFinalProjectTask(Project project) {
        //project.plugins.apply(JacocoPlugin)
        project.apply plugin: 'jacoco'
        project.jacoco.toolVersion = "0.7.6.201602180812"
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
                    it != null
                }
            })
        }
    }

}