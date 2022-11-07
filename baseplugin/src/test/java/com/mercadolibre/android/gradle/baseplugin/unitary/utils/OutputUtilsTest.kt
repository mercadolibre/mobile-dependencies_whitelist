package com.mercadolibre.android.gradle.baseplugin.unitary.utils

import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import io.mockk.every
import io.mockk.mockk
import java.io.File

class OutputUtilsTest {

    @org.junit.After
    fun after() {
        File("build/test.txt").delete()
    }

    @org.junit.Test
    fun `When OutputUtils is called to create a file report the file content is correct`() {
        val mockedFile = mockk<File>(relaxed = true) {
            every { path } returns "build/test.txt"
        }

        OutputUtils.writeAReportMessage("Title : Message", mockedFile)

        assert(File("build/test.txt").readText().contains("Title"))
        assert(File("build/test.txt").readText().contains("Message"))
    }

    @org.junit.Test
    fun `When OutputUtils is called to write in a file report the file content is correct`() {
        val mockedFile = mockk<File>(relaxed = true) {
            every { path } returns "build/test.txt"
        }

        OutputUtils.writeAReportMessage("Title : Message", mockedFile)

        assert(File("build/test.txt").readText().contains("Title"))
        assert(File("build/test.txt").readText().contains("Message"))

        OutputUtils.writeAReportMessage("Append Text", mockedFile)

        assert(File("build/test.txt").readText().contains("Append Text"))
    }
}
