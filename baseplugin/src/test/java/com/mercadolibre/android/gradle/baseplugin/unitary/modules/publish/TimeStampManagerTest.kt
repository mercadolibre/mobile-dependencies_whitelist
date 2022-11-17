package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_GENERATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_ZONE
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeStampManagerTest : AbstractPluginManager() {

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.Test
    fun `Get a time stamp`() {
        val createdTime = TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR) // Create

        assert(createdTime === TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR)) // Get
    }

    @org.junit.Test
    fun `Delete a time stamp`() {
        val createdTime = TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR) // Create

        TimeStampManager.deleteTimeStamp() // Delete

        assert(TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR) !== createdTime)
    }

    @org.junit.Test
    fun `Delete a time stamp if not exist`() {
        TimeStampManager.deleteTimeStamp() // Delete
        assert(TimeStampManager.getOrCreateTimeStamp(PUBLISHING_TIME_GENERATOR) == SimpleDateFormat(PUBLISHING_TIME_GENERATOR)
            .apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date()))
    }
}
