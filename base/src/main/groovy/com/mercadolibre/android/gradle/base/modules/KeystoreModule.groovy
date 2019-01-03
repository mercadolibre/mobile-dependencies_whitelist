package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

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
        final File directory = project.mkdir("${project.buildDir}${File.separator}${DIRECTORY_NAME}")
        final File keystore = project.file("${directory.absolutePath}${File.separator}${FILE_NAME}")

        // Write the keystore file into the build directory
        final InputStream inputStream = KeystoreModule.class.getResourceAsStream("${File.separator}${DIRECTORY_NAME}${File.separator}${FILE_NAME}")
        keystore.withOutputStream { outputStream ->
            int read = 0
            byte[] bytes = new byte[BUFFER_LENGTH]
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read)
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