package com.mercadolibre.android.gradle.baseplugin.unitary.modules.pluginDescription

import com.mercadolibre.android.gradle.baseplugin.core.action.modules.pluginDescription.AbstractModulePluginDescription
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import io.mockk.mockk
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AbstractModulePluginDescriptionTest {

    @org.junit.Test
    fun `When the any AbstractModulePluginDescriptionTest is created workd`() {
        val pluginDescription = PluginDescriptionClassTest()
        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.makeMessage(ANY_NAME, ANY_NAME)
        pluginDescription.configureTask(mockk(relaxed = true))
    }

    class PluginDescriptionClassTest : AbstractModulePluginDescription(ANY_NAME, ANY_NAME, { ANY_NAME })
}
