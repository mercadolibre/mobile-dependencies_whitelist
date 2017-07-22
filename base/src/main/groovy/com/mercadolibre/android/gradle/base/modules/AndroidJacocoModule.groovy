package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Created by saguilera on 7/22/17.
 */
class AndroidJacocoModule extends BaseJacocoModule {

    @Override
    void configure(Project project) {
        super.configure(project)

        project.android {
            testOptions {
                unitTests.all {
                    jacoco {
                        includeNoLocationClasses = true
                    }
                }
            }
        }

        createJacocoReportTasks()
    }

    /**
     * Creates the tasks to generate Jacoco report, one per variant depending on Unit and Instrumentation tests.
     * [incubating]
     */
    private void createJacocoReportTasks() {
        def variants
        try {
            variants = project.android.applicationVariants
        } catch (Exception e) {
            variants = project.android.libraryVariants
        }

        variants.all { variant ->
            //Define local variables to avoid accessing multiple times to the buildType object.
            def buildTypeName = variant.buildType.name
            def flavorName = variant.flavorName
            def capitalizedBuildTypeName = buildTypeName.capitalize()
            def capitalizedFlavorName = flavorName.capitalize()

            def taskName = "jacoco${capitalizedFlavorName}${capitalizedBuildTypeName}"
            def testTaskName = "test${capitalizedFlavorName}${capitalizedBuildTypeName}UnitTest"

            //Create and retrieve necesary tasks
            def jacocoTask = project.tasks.create taskName, JacocoReport
            def unitTest = project.tasks.findByName(testTaskName)

            //Define JacocoTasks and it's configuration
            jacocoTask.description = "Generate Jacoco code coverage report after running tests for ${flavorName}${capitalizedBuildTypeName} flavor."
            jacocoTask.group = "Reporting"

            //By convention this is the sources folder
            jacocoTask.sourceDirectories = project.files("src/main/java")

            //Here is where execution data files are created. ConnectedAndroidTest and Test tasks generates them.
            jacocoTask.executionData = project.files("build/jacoco/${testTaskName}.exec")

            def jacocoDirectory = "./build/intermediates/classes/"

            if (flavorName != null && flavorName != "") {
                jacocoDirectory += "${flavorName}/"
            }

            jacocoDirectory += "${buildTypeName}"

            //Ignore auto-generated classes
            jacocoTask.classDirectories = project.fileTree(dir: jacocoDirectory, excludes: [
                    '**/R.class',
                    '**/R$*.class',
                    '**/BuildConfig.class',
                    '**/*$ViewInjector*.*',
                    '**/*$ViewBinder*.*',
                    '**/Manifest*.*',
                    '**/*$Lambda$*.*',
                    '**/*Module.*',
                    '**/*Dagger*.*',
                    '**/*MembersInjector*.*',
                    '**/*_Provide*Factory*.*',
                    '**/*_Factory*.*',
                    '**/*$*$*.*'
            ])

            //Enable both reports
            jacocoTask.reports.xml.enabled = true
            jacocoTask.reports.html.enabled = true

            jacocoTask.dependsOn unitTest
            //If testCoverage is not enabled, Android Jacoco' plugin will not instrumentate project classes
            if (variant.buildType.testCoverageEnabled) {
                project.logger.warn("WARNING: You should DISABLE \"android.buildTypes.${buildTypeName}.testCoverageEnabled\" in your build.gradle in order to make \"${taskName}\" run succesfully in \"${project.name}\".")
            }
        }
    }

}
