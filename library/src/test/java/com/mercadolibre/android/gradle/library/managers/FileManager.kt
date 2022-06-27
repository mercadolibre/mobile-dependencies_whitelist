package com.mercadolibre.android.gradle.library.managers

import java.io.File
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder

class FileManager(val tmpFolder: TemporaryFolder) {

    fun readFile(pathFile: String): String {
        return File(File("./").absolutePath.split("library")[0] + pathFile).readText()
    }

    fun createFile(name: String, content: String) {
        tmpFolder.newFile(name).writeText(content)
    }

}