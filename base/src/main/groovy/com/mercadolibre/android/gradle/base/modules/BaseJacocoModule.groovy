package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPlugin

/**
 * Base jacoco module for creating a global task that will create all the available reports for each testing task
 *
 * Created by saguilera on 7/22/17.
 */
abstract class BaseJacocoModule implements Module {

    public static final String JACOCO_FULL_REPORT_TASK = 'jacocoFullReport'

    protected Project project

    @Override
    void configure(Project project) {
        this.project = project

        project.with {
            apply plugin:JacocoPlugin

            jacoco {
                toolVersion = '0.8.2'
            }

            tasks.withType(Test).configureEach {
                testLogging {
                    events "FAILED"
                    exceptionFormat "full"
                }
            }

            tasks.register(JACOCO_FULL_REPORT_TASK).configure {
                it.group 'reporting'
            }
        }
    }

    static void createExtension(Project project) {
        String extensionName = JacocoConfigurationExtension.simpleName.replaceAll("Extension", '')
        extensionName = (Character.toLowerCase(extensionName.charAt(0)) as String) + extensionName.substring(1)
        project.extensions.create(extensionName, JacocoConfigurationExtension)
        project.subprojects.each {
            it.extensions.create(extensionName, JacocoConfigurationExtension)
        }
    }
}
