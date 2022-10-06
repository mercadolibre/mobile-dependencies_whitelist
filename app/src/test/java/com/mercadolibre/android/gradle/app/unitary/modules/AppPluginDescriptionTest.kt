package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription.AppPluginDescriptionExtensionsModule
import com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.app.managers.ANY_NAME
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import io.mockk.mockk
import io.mockk.unmockkAll
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppPluginDescriptionTest {

    @org.junit.Before
    fun setup() {
        unmockkAll()
    }

    @org.junit.Test
    fun `When the AppPluginDescriptionModule is called return the correct message`() {
        val pluginDescription = AppPluginDescriptionModule()
        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME, ANY_NAME)
        pluginDescription.configureTask(task)

        assert(pluginDescription.makeMessage(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")
        assert(pluginDescription.content() == AppModuleConfigurer().getModules("App Modules", ModuleProvider.provideAppAndroidModules()))
    }

    @org.junit.Test
    fun `When the AppPluginDescriptionExtension is called then return the correct content`() {
        val pluginDescription = AppPluginDescriptionExtensionsModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME, ANY_NAME)
        pluginDescription.configureTask(task)
        assert(pluginDescription.makeMessage(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")

        var names = ""
        for (extensionProvider in ModuleProvider.provideAppAndroidModules()) {
            names += "${extensionProvider.getExtensionName()}, "
        }

        assert(pluginDescription.content() == names.substring(0, names.length - 2).ansi(ANSI_GREEN))
    }
}
