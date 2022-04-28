package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Module that signs the application in the debug buildType with a default public keystore.
 *
 * This makes all applications signed in their DEBUG mode to share the same public certificate
 *
 * Created by saguilera on 7/22/17.
 */
class KeystoreModule implements Module {

    private final int BUFFER_LENGTH = 1024

    private final String DIRECTORY_NAME = "keystores"
    private final String FILE_NAME = "debug_keystore"

    private final String KEY_STORE_PASSWORD = "android"
    private final String KEY_ALIAS = "androiddebugkey"
    private final String KEY_PASSWORD = "android"

    @Override
    void configure(Project project) {
        // Create info we will rely on
        final File directory = project.file("${project.buildDir}${File.separator}${DIRECTORY_NAME}")
        final File keystore = project.file("${directory.absolutePath}${File.separator}${FILE_NAME}")

        // Define a task to unpack the keystore were we will place it
        final TaskProvider<Task> unpackKeystoreTask = project.tasks.register("unpackDebugKeystore")

        unpackKeystoreTask.configure {
            group = 'keystore'
            description = 'Unpack the debug keystore into the build directory of the project'
            it.outputs.file keystore
            doLast {
                // Create the directory
                directory.mkdirs()

                // Write the keystore file into the build directory
                final InputStream inputStream = KeystoreModule.class.getResourceAsStream("/${DIRECTORY_NAME}/${FILE_NAME}")
                keystore.withOutputStream { outputStream ->
                    int read = 0
                    byte[] bytes = new byte[BUFFER_LENGTH]
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read)
                    }
                }
            }
        }

        // Make every validate signing task depend on it
        project.android.applicationVariants.all { variant ->
            final String validateTaskName = "validateSigning${variant.name.capitalize()}"
            if (project.tasks.names.contains(validateTaskName)) {
                project.tasks.named(validateTaskName).configure {
                    dependsOn unpackKeystoreTask
                }
            }
        }

        // Setup project signing configurations with the keystore file and credentials
        project.android {
            signingConfigs {
                debug {
                    storeFile keystore
                    storePassword KEY_STORE_PASSWORD
                    keyAlias KEY_ALIAS
                    keyPassword KEY_PASSWORD
                }
            }

            buildTypes {
                debug {
                    signingConfig signingConfigs.debug
                }
            }
        }
    }

}