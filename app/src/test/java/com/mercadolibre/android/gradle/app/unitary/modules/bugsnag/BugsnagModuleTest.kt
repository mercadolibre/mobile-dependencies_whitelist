package com.mercadolibre.android.gradle.app.unitary.modules.bugsnag

import com.mercadolibre.android.gradle.app.core.action.modules.bugsnag.BugsnagExtension
import com.mercadolibre.android.gradle.app.core.action.modules.bugsnag.BugsnagModule
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BugsnagModuleTest : AbstractPluginManager() {

    private val bugsnagModule = BugsnagModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        bugsnagModule.createExtension(root)
    }

    @org.junit.Test
    fun `When the BugsnagModule is called configure the project and is disabled`() {
        bugsnagModule.configure(root)
    }

    @org.junit.Test
    fun `When the BugsnagModule is called configure the project`() {
        findExtension<BugsnagExtension>(root)?.apply {
            enabled = true
        }

        bugsnagModule.configure(root)
    }
}
