package com.mercadolibre.android.gradle.base

import groovy.io.FileType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Created by nbarrios on 8/10/16.
 */
class FirstCoveragePlugin implements Plugin<Project> {

    private static flavor = ""
    private static directory = ""
    /**
     * The project.
     */
    private Project project;
    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {

        this.project = project

        def subprojectsNames = []

        this.project.getSubprojects().each {
            subprojectsNames.add(it.getName())
        }

        project.apply plugin: 'jacoco'
        project.jacoco.toolVersion = "0.7.1.201405082137"
        project.apply plugin: 'com.github.kt3k.coveralls'
        def execList = []
        execList = getReports()
        createJacocoMergeTask(execList)
        createJacocoFinalProjectTask(execList)
        createCoveragePost(execList)
    }

    //Generates one .exec merging every .exec generated for the subprojects
    private void createJacocoMergeTask(subprojects) {
        def jacocoTask = project.tasks.create "jacocoMergeReport", JacocoMerge
        jacocoTask.description = "Generate Jacoco merged report"
        jacocoTask.group = "Reporting"
        subprojects.each {
            if (it.exists()) {
                jacocoTask.executionData project.files(it)
            }
        }
    }

    //Uses the merged .exec to create a final report for the whole project
    private void createJacocoFinalProjectTask(subprojects) {
        def jacocoTask = project.tasks.create "jacocoFinalProjectReport", JacocoReport
        jacocoTask.description = "Generate Jacoco final report"
        jacocoTask.group = "Reporting"
        jacocoTask.dependsOn "jacocoMergeReport"
        def listsrc = []
        def listclass = []
        subprojects.each {
            if (it.exists()) {
                def projName = it.toString().replaceAll("/build/jacoco/test${flavor}UnitTest.exec", "")
                listsrc << "${projName}/src/main/java"
                listclass << "${projName}/build/intermediates/classes/${directory}"
            }
        }
        if (!(subprojects.isEmpty())) {
            if (subprojects.size() > 1) {
                jacocoTask.executionData = project.files("build/jacoco/jacocoMergeReport.exec")
            } else {
                jacocoTask.executionData = project.files(subprojects[0])
            }
        }

        jacocoTask.sourceDirectories = project.files(listsrc)
        jacocoTask.classDirectories = project.files(listclass)
        //Here is where execution data files are created. ConnectedAndroidTest and Test tasks generates them.

        //Ignore auto-generated classes
        jacocoTask.classDirectories = project.files(jacocoTask.classDirectories.files.collect {
            project.fileTree(dir: it,
                    exclude: [
                            '**/R.class',
                            '**/R$*.class',
                            '**/BuildConfig.class',
                    ])
        })

        //Enable both reports
        jacocoTask.reports.xml.enabled = true
        jacocoTask.reports.html.enabled = true

        jacocoTask.onlyIf = {
            true
        }
    }

    //Executes post to coveralls.io of the resulting .exec file
    private void createCoveragePost(subprojects) {
        def task = this.project.getTasksByName("coveralls", false)
        task = task[0]
        task.description = "Post coverage report to coveralls.io"
        task.group = "Reporting"

        this.project.findProperty("coveralls").jacocoReportPath = 'build/reports/jacoco/jacocoFinalProjectReport/jacocoFinalProjectReport.xml'
        def listsrc = []
        subprojects.each {
            subproject ->
                def projName = subproject.toString().replaceAll("/build/jacoco/test${flavor}UnitTest.exec", "")
                if (project.file("${projName}/build/jacoco/test${flavor}UnitTest.exec").exists()) {
                    listsrc << "${projName}/src/main/java"
                }
        }
        this.project.findProperty("coveralls").sourceDirs = listsrc
        task.onlyIf({
            System.env.
                    'CI'
        })
    }

    //Gets all .exec report files generated
    private getReports() {
        def execList = []
        if (project.hasProperty('flavor') && project.hasProperty('directory')) {
            flavor = project.getProperty('flavor')
            directory = project.getProperty('directory')
        }
        def dir = new File("${this.project.getRootDir()}")
        println "Using ${flavor}"
        dir.eachFileRecurse(FileType.FILES) {
            file ->
                if (file.getName().contains("test${flavor}UnitTest.exec"))
                    execList << file
        }
        return execList
    }

}