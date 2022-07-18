package com.mercadolibre.android.gradle.baseplugin.managers

import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import java.io.File

class FileManager(val tmpFolder: TemporaryFolder) {

    fun getFile(pathFile: String): File {
        return File(File("./").absolutePath.split("baseplugin")[0] + pathFile)
    }

    fun readFile(pathFile: String): String = File(File("./").absolutePath.split("baseplugin")[0] + pathFile).readText()

    fun createFile(name: String, content: String) {
        tmpFolder.newFile(name).writeText(content)
    }
}
