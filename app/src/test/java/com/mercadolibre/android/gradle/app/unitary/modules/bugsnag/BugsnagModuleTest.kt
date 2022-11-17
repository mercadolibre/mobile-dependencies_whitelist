package com.mercadolibre.android.gradle.app.unitary.modules.bugsnag

import com.bugsnag.android.gradle.BugsnagPluginExtension
import com.mercadolibre.android.gradle.app.core.action.modules.bugsnag.BugsnagModule
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.core.basics.ModuleOnOffExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.BUGSNAG_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.BUGSNAG_RETRY_CONVENTION
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BugsnagModuleTest : AbstractPluginManager() {

    private val bugsnagModule = BugsnagModule()

    @org.junit.Test
    fun `When the BugsnagModule is called create her extension`() {
        val project = mockk<Project>(relaxed = true)

        bugsnagModule.createExtension(project)

        verify { project.extensions.create(BUGSNAG_EXTENSION, ModuleOnOffExtension::class.java) }
    }

    @org.junit.Test
    fun `When the BugsnagModule is called configure the project and is disabled`() {
        val project = mockk<Project>(relaxed = true)
        val extension = mockk<ModuleOnOffExtension>(relaxed = true) {
            enabled = true
        }
        val extensionPlugin = mockk<BugsnagPluginExtension>(relaxed = true)

        every { project.extensions.findByType(ModuleOnOffExtension::class.java) } returns extension
        every { project.extensions.findByType(BugsnagPluginExtension::class.java) } returns extensionPlugin
        every { extension.enabled } returns false

        bugsnagModule.configure(project)

        verify { extensionPlugin.retryCount.convention(BUGSNAG_RETRY_CONVENTION) wasNot Called }
    }

    @org.junit.Test
    fun `When the BugsnagModule is called configure the project`() {
        val project = mockk<Project>(relaxed = true)
        val extension = mockk<ModuleOnOffExtension>(relaxed = true) {
            enabled = true
        }
        val extensionPlugin = mockk<BugsnagPluginExtension>(relaxed = true)

        every { project.extensions.findByType(ModuleOnOffExtension::class.java) } returns extension
        every { project.extensions.findByType(BugsnagPluginExtension::class.java) } returns extensionPlugin
        every { extension.enabled } returns true

        bugsnagModule.configure(project)

        verify { extensionPlugin.retryCount.convention(BUGSNAG_RETRY_CONVENTION) }
        verify { extensionPlugin.variantFilter(any()) }
    }
}
