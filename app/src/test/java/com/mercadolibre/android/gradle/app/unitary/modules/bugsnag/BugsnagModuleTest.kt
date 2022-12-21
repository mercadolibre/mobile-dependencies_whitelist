package com.mercadolibre.android.gradle.app.unitary.modules.bugsnag

import com.bugsnag.android.gradle.BugsnagPluginExtension
import com.mercadolibre.android.gradle.app.core.action.modules.bugsnag.BugsnagExtension
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
    fun `When the BugsnagModule is called and is enabled`() {
        val project = mockk<Project>(relaxed = true)
        val extensionPlugin = mockk<BugsnagPluginExtension>(relaxed = true)
        val extension = BugsnagExtension().apply {
            enabled = true
        }

        every { project.extensions.findByType(BugsnagExtension::class.java) } returns extension
        every { project.extensions.findByType(BugsnagPluginExtension::class.java) } returns extensionPlugin

        bugsnagModule.executeModule(project)

        assert(extension.enabled)

        verify { extensionPlugin.retryCount.convention(BUGSNAG_RETRY_CONVENTION) }
        verify { extensionPlugin.variantFilter(any()) }
    }

    @org.junit.Test
    fun `When the BugsnagModule is called create her extension`() {
        val project = mockk<Project>(relaxed = true)
        val extension = BugsnagExtension()

        every { project.extensions.create(BUGSNAG_EXTENSION, BugsnagExtension::class.java) } returns extension
        every { project.extensions.findByType(BugsnagExtension::class.java) } returns extension

        bugsnagModule.createExtension(project)
        bugsnagModule.executeModule(project)

        assert(!extension.enabled)
        verify { project.extensions.create(BUGSNAG_EXTENSION, BugsnagExtension::class.java) }
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
