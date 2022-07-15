package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
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
        TimeStampManager.getOrCreateTimeStamp() // Create
        TimeStampManager.getOrCreateTimeStamp() // Get
    }

    @org.junit.Test
    fun `Delete a time stamp`() {
        TimeStampManager.getOrCreateTimeStamp() // Create
        TimeStampManager.deleteTimeStamp() // Delete
    }

    @org.junit.Test
    fun `Delete a time stamp if not exist`() {
        TimeStampManager.deleteTimeStamp() // Delete
    }
}
