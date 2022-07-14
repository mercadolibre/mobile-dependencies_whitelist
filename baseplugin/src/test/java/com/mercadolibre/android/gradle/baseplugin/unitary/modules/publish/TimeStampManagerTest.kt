package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TimeStampManager
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeStampManagerTest: AbstractPluginManager() {

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)
    }

    @org.junit.Test
    fun `Get a time stamp`() {
        TimeStampManager.getOrCreateTimeStamp(projects[LIBRARY_PROJECT]!!) // Create
        TimeStampManager.getOrCreateTimeStamp(projects[LIBRARY_PROJECT]!!) // Get
    }

    @org.junit.Test
    fun `Delete a time stamp`() {
        TimeStampManager.getOrCreateTimeStamp(projects[LIBRARY_PROJECT]!!) // Create
        TimeStampManager.deleteTimeStampFile(projects[LIBRARY_PROJECT]!!) // Delete
    }

    @org.junit.Test
    fun `Delete a time stamp if not exist`() {
        TimeStampManager.deleteTimeStampFile(projects[LIBRARY_PROJECT]!!) // Delete
    }
}