package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_FILE
import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeStampManagerTest {

    @org.junit.Test
    fun `Get a time stamp`() {
        val project = mockk<Project>(relaxed = true)
        val file = mockk<File>(relaxed = true)


        every { project.rootProject.file(PUBLISHING_TIME_FILE) } returns file

        TimeStampManager.getOrCreateTimeStamp(project)
    }

}