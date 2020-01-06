package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Check module for Kotlin subprojects
 */
class KotlinCheckModule implements Module {


    //TODO: version should be variable (from properties)
    private static final String DETEKT_VERSION = '1.3.1'
    private static final String DETEKT_PACKAGE_NAME = 'io.gitlab.arturbosch.detekt'
    private static final String DETEKT_MODULE_NAME = 'detekt-formatting'
    private static final String DETEKT_LINT_TASK_NAME = 'detekt'
    private static final String DETEKT_CONFIG_TASK_NAME = 'detektGenerateConfig'
    //TODO: fix ../
    //TODO: check flavours support
    //TODO: check full path
    private static final String DETEKT_CONFIG_FILE_PATH = '../config/detekt/detekt.yml'
    private static final String DETEKT_REPORT_HTML_FILE_PATH = '../build/reports/detekt_report.html'
    private static final String DETEKT_REPORT_XML_FILE_PATH = '../build/reports/detekt_report.xml'
    //TODO: filter only *.ktb
    private static final String KOTLIN_FILES_REGEX = '.'

    @Override
    void configure(Project project) {
        String projectRootDir = project.getRootDir().getName()

        project.with {
            apply plugin: DETEKT_PACKAGE_NAME

            dependencies {
                detektPlugins DETEKT_PACKAGE_NAME + ":" + DETEKT_MODULE_NAME + ":" + DETEKT_VERSION
            }

            detekt {
                input = files(KOTLIN_FILES_REGEX)
                config = files(DETEKT_CONFIG_FILE_PATH)
                //TODO: filters not working
                //filters = ".*test.*,.*/resources/.*,.*/tmp/.*"

                reports {
                    xml {
                        enabled = true
                        destination = file(DETEKT_REPORT_XML_FILE_PATH)
                    }
                    html {
                        enabled = true
                        destination = file(DETEKT_REPORT_HTML_FILE_PATH)
                    }
                }
            }
        }

        def file = new File(DETEKT_CONFIG_FILE_PATH)
        if (!(file.exists() && file.canRead())) {
            project.tasks.getByName(DETEKT_LINT_TASK_NAME).dependsOn DETEKT_CONFIG_TASK_NAME
        }
    }
}
