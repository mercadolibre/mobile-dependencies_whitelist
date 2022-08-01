package com.mercadolibre.android.gradle.baseplugin.unitary.modules.buildscan

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin
import com.gradle.scan.plugin.BuildScanExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.buildscan.BuildScanModule
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVER_URL
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVICES_AGREE
import com.mercadolibre.android.gradle.baseplugin.core.components.GRADLE_ENTERPRISE_SERVICES_URL
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.initialization.Settings
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BuildScanTest : AbstractPluginManager() {

    val buildScan = BuildScanModule()

    @org.junit.Test
    fun `When the BuildScanModule is called configure build scan extension the project`() {
        val extension = mockk<BuildScanExtension>(relaxed = true)
        buildScan.configBuildScanExtension(extension, ANY_NAME, true)
        buildScan.configBuildScanExtension(extension, ANY_NAME, false)

        verify { extension.publishAlways() }
        verify { extension.termsOfServiceUrl = GRADLE_ENTERPRISE_SERVICES_URL }
        verify { extension.termsOfServiceAgree = GRADLE_ENTERPRISE_SERVICES_AGREE }
        verify { extension.server = GRADLE_ENTERPRISE_SERVER_URL }
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure her background tasks`() {
        val extension = mockk<BuildScanExtension>(relaxed = true)
        buildScan.configBackground(extension)

        verify { extension.value(any(), any()) }
    }

    @org.junit.Test
    fun `When the BuildScanModule is called configure the setting`() {
        val settings = mockk<Settings>(relaxed = true)

        every { settings.extensions.findByType(GradleEnterpriseExtension::class.java) } returns null

        buildScan.configure(settings)

        every { settings.extensions.findByType(GradleEnterpriseExtension::class.java) } returns mockk(relaxed = true)

        buildScan.configure(settings)

        verify { settings.plugins.apply(GradleEnterprisePlugin::class.java) }
        verify { settings.extensions.findByType(GradleEnterpriseExtension::class.java) }
    }
}
