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

    private final String directoryName = "keystores"
    private final String fileName = "debug_keystore"

    private final String KEY_STORE_PASSWORD = "android"
    private final String KEY_ALIAS = "androiddebugkey"
    private final String KEY_PASSWORD = "android"

    @Override
    void configure(Project project) {
        final File directory = project.mkdir("${project.buildDir}${File.separator}${directoryName}")
        final File keystore = project.file("${directory.absolutePath}${File.separator}${fileName}")

        // Write the keystore file into the build directory
        final InputStream inputStream = KeystoreModule.class.getResourceAsStream("${File.separator}${directoryName}${File.separator}${fileName}")
        keystore.withOutputStream { outputStream ->
            int read = 0
            byte[] bytes = new byte[BUFFER_LENGTH]
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read)
            }
        }

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