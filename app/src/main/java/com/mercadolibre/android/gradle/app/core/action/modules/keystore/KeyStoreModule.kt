package com.mercadolibre.android.gradle.app.core.action.modules.keystore

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.DEBUG_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.DIRECTORY_NAME
import com.mercadolibre.android.gradle.baseplugin.core.components.FILE_NAME_DEBUG_KEY
import com.mercadolibre.android.gradle.baseplugin.core.components.KEY_ALIAS
import com.mercadolibre.android.gradle.baseplugin.core.components.KEY_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.KEY_STORE_PASSWORD
import com.mercadolibre.android.gradle.baseplugin.core.components.UNPACK_DEBUG_KEY_STORE_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.UNPACK_DEBUG_KEY_STORE_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.UNPACK_DEBUG_KEY_STORE_TASK
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.File

/**
 * KeyStoreModule is in charge of providing the key when generating a release.
 */
class KeyStoreModule(private val isApp: Boolean) : Module, ExtensionGetter() {
    override fun configure(project: Project) {
        if (isApp) {
            // Create info we will rely on
            val directory = project.file("${project.buildDir}${File.separator}$DIRECTORY_NAME")
            val keystore = project.file("${directory.absolutePath}${File.separator}$FILE_NAME_DEBUG_KEY")
            val fileDir = "${project.projectDir}${File.separator}$FILE_NAME_DEBUG_KEY"

            // Define a task to unpack the keystore were we will place it
            val unpackKeystoreTask = project.tasks.register(UNPACK_DEBUG_KEY_STORE_TASK)

            unpackKeystoreTask.configure {
                group = UNPACK_DEBUG_KEY_STORE_GROUP
                description = UNPACK_DEBUG_KEY_STORE_DESCRIPTION

                outputs.file(keystore)

                doLast {
                    writeFile(project, fileDir, keystore)
                }
            }

            findExtension<AppExtension>(project)?.apply {
                applicationVariants.all {
                    val validateTaskName = "validateSigning${this.name.capitalize()}"
                    if (project.tasks.names.contains(validateTaskName)) {
                        project.tasks.named(validateTaskName).configure {
                            dependsOn(unpackKeystoreTask)
                        }
                    }
                }
            }

            findExtension<BaseExtension>(project)?.apply {
                signingConfigs[DEBUG_CONSTANT].apply {
                    storeFile(keystore)
                    storePassword(KEY_STORE_PASSWORD)
                    keyAlias(KEY_ALIAS)
                    keyPassword(KEY_PASSWORD)
                }

                buildTypes[DEBUG_CONSTANT].signingConfig = signingConfigs[DEBUG_CONSTANT]
            }
        }
    }

    fun writeFile(project: Project, fileDir: String, keystore: File) {
        val inputStream = project.file(fileDir).inputStream()
        inputStream.copyTo(keystore.outputStream())
        inputStream.close()
    }
}
