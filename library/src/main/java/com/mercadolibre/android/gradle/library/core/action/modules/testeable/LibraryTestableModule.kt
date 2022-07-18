package com.mercadolibre.android.gradle.library.core.action.modules.testeable

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.BUILD_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_REPORT_FLAVOR_TEST_TASK_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_REPORT_TASK_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.UNIT_TEST_FLAVOR_TEST_TASK_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.UNIT_TEST_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.UNIT_TEST_TASK_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.VARIANT_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

/**
 * LibraryTestableModule This is the module in charge of configuring all the tests within the library module.
 */
class LibraryTestableModule : Module, ExtensionGetter() {
    /**
     * This method is in charge of configuring the tests within the library module
     */
    override fun configure(project: Project) {
        findExtension<BaseExtension>(project)?.apply {
            findExtension<LibraryExtension>(project)?.apply {
                for (productFlavor in productFlavors) {
                    for (variant in libraryVariants) {
                        val variantName = variant.name.capitalize()
                        val flavorName = productFlavor.name.capitalize()

                        if (!variantName.startsWith(flavorName)) {
                            return
                        }

                        val tasks = listOf(
                            FlavorTesteableTask(UNIT_TEST_FLAVOR_TEST_TASK_NAME, UNIT_TEST_GROUP, UNIT_TEST_TASK_DESCRIPTION),
                            FlavorTesteableTask(JACOCO_REPORT_FLAVOR_TEST_TASK_NAME, JACOCO_GROUP, JACOCO_REPORT_TASK_DESCRIPTION)
                        )

                        for (task in tasks) {
                            val build = variantName.split(flavorName)[0]
                            val flavorTaskName = task.taskName.replace(VARIANT_CONSTANT, variantName)
                            val genericTaskName = task.taskName.replace(VARIANT_CONSTANT, build)

                            val flavorTask = project.tasks.named(flavorTaskName)

                            if (project.tasks.names.contains(genericTaskName)) {
                                project.tasks.named(genericTaskName).configure {
                                    dependsOn(flavorTask)
                                }
                            } else {
                                project.tasks.register(genericTaskName).configure {
                                    group = task.group
                                    description = task.description.replace(BUILD_CONSTANT, build)
                                    dependsOn(flavorTask)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
