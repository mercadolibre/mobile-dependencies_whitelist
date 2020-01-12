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
    private static final String DETEKT_CONFIG_FILE_NAME = 'detekt.yml'
    private static final String DETEKT_CONFIG_FILE_PATH = 'config/detekt/'
    private static final String DETEKT_CUSTOM_CONFIG_FILE_PATH = './'
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
                config = project.rootProject.files(DETEKT_CUSTOM_CONFIG_FILE_PATH + DETEKT_CONFIG_FILE_NAME)

                // Enabling XML and HTML reports
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

        configureDetektConfigTask(project)
        makeDetektGenerateConfigAlways(project)
    }

    /**
     * Making Detekt's default config generation task to move the generated config to our custom path.
     * This is because this task doesn't generates the file in the custom path set in the configuration.
     *
     * @param project to get the task and move the file
     */
    private static void configureDetektConfigTask(Project project) {
        project.tasks.getByName(DETEKT_CONFIG_TASK_NAME).doLast {
            def file = project.rootProject.file(DETEKT_CUSTOM_CONFIG_FILE_PATH + DETEKT_CONFIG_FILE_NAME)
            if (!file.exists()) {
                project.rootProject.ant.move file: project.rootProject.file(DETEKT_CONFIG_FILE_PATH + DETEKT_CONFIG_FILE_NAME).toString(),
                        todir: project.rootProject.file(DETEKT_CUSTOM_CONFIG_FILE_PATH).toString()
            }
        }
    }

    /**
     * This makes the main Detekt's lint task to depend on the config's generation task.
     * We made this because if we run Detekt without the configuration file, it fails.
     * It's attached to {@link #DETEKT_LINT_TASK_NAME} because it is the one that is called when `check` is called
     *
     * @param project to set Detekt's config task to run always
     */
    private static void makeDetektGenerateConfigAlways(Project project) {
        project.tasks.getByName(DETEKT_LINT_TASK_NAME).dependsOn DETEKT_CONFIG_TASK_NAME
    }
}
