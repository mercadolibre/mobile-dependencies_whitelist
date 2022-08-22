package com.mercadolibre.android.gradle.baseplugin.core.action.modules.projectInfo

import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_YELLOW
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.PROJECT_INFO_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PROJECT_INFO_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.SEPARATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * The ProjectInfoModule is responsible for shows the configurations of all subprojects.
 */
class ProjectInfoModule : Module() {

    /**
     * This is the method in charge of adding the task that shows the configurations of all subprojects.
     */
    override fun configure(project: Project) {
        configureTask(project.tasks.register(PROJECT_INFO_TASK).get())
    }

    private fun configureTask(task: Task) {
        with(task) {
            group = MELI_GROUP
            description = PROJECT_INFO_DESCRIPTION
            doLast {
                printConfiguration(project)
            }
        }
    }

    /**
     * This is the method in charge to shows the configurations of all subprojects.
     */
    fun getInfo(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            println(
                """
                - ${"Compile SDK".ansi(ANSI_YELLOW)}            $ARROW $compileSdkVersion
                - ${"Build Tools Version".ansi(ANSI_YELLOW)}    $ARROW $buildToolsVersion
                - ${"Min Sdk Version".ansi(ANSI_YELLOW)}        $ARROW ${defaultConfig.minSdkVersion!!.apiString}
                - ${"Target Sdk Version".ansi(ANSI_YELLOW)}     $ARROW ${defaultConfig.targetSdkVersion!!.apiString}
                - ${"Source Compatibility".ansi(ANSI_YELLOW)}   $ARROW ${compileOptions.sourceCompatibility}
                - ${"Target Compatibility".ansi(ANSI_YELLOW)}   $ARROW ${compileOptions.targetCompatibility}
                
                - ${"Consumer Proguard Files".ansi(ANSI_YELLOW)}         $ARROW ${defaultConfig.consumerProguardFiles}
            """
            )
        }
    }

    /**
     * This is the method in to get the list of the configurations of all subprojects.
     */
    fun printConfiguration(project: Project) {
        for (subProject in project.subprojects) {
            println(SEPARATOR)
            print("${subProject.name.ansi(ANSI_GREEN)} - ")
            getInfo(subProject)
        }
    }
}
