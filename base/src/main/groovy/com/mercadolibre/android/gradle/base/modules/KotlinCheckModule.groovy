package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Check module for Kotlin subprojects
 */
class KotlinCheckModule implements Module {
    private static final String DETEKT_VERSION = '1.3.1'
    private static final String DETEKT_PACKAGE_NAME = 'io.gitlab.arturbosch.detekt'
    private static final String DETEKT_MODULE_NAME = 'detekt-formatting'
    private static final String DETEKT_LINT_TASK_NAME = 'detekt'
    private static final String DETEKT_CONFIG_TASK_NAME = 'detektGenerateConfig'
    private static final String DETEKT_CONFIG_FILE_PATH = 'config/detekt/detekt.yml'
    private static final String DETEKT_REPORT_HTML_FILE_PATH = 'build/reports/detekt_report.html'
    private static final String DETEKT_REPORT_XML_FILE_PATH = 'build/reports/detekt_report.xml'
    private static final String KOTLIN_FILES_REGEX = '.'

    @Override
    void configure(Project project) {
        project.with {
            apply plugin: DETEKT_PACKAGE_NAME

            dependencies {
                detektPlugins DETEKT_PACKAGE_NAME + ":" + DETEKT_MODULE_NAME + ":" + DETEKT_VERSION
            }

            detekt {
                input = project.rootProject.files(KOTLIN_FILES_REGEX)
                config = project.rootProject.files(DETEKT_CONFIG_FILE_PATH)

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

        project.tasks.getByName(DETEKT_LINT_TASK_NAME).dependsOn DETEKT_CONFIG_TASK_NAME
    }
}
