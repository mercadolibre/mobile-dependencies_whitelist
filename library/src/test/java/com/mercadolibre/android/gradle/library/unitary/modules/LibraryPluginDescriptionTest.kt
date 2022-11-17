package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.baseplugin.core.components.ANSI_GREEN
import com.mercadolibre.android.gradle.baseplugin.core.components.ansi
import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription.LibraryPluginDescriptionExtensionsModule
import com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.managers.ANY_NAME
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import io.mockk.mockk
import io.mockk.unmockkAll
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryPluginDescriptionTest {

    @org.junit.Before
    fun after() {
        unmockkAll()
    }

    @org.junit.Test
    fun `When the LibraryPluginDescriptionModule is called then return correct messages`() {
        val pluginDescription = LibraryPluginDescriptionModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME, ANY_NAME)

        assert(pluginDescription.makeLog(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")
        assert(
            pluginDescription.content() ==
                LibraryModuleConfigurer().getModules("Library Module", ModuleProvider.provideLibraryAndroidModules())
        )
    }

    @org.junit.Test
    fun `When the LibraryPluginDescriptionExtension is called then return the correct content`() {
        val pluginDescription = LibraryPluginDescriptionExtensionsModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME, ANY_NAME)

        assert(pluginDescription.makeLog(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")

        var names = ""
        for (extensionProvider in ModuleProvider.provideLibraryAndroidModules()) {
            names += "${extensionProvider.getExtensionName()}, "
        }

        assert(pluginDescription.content() == names.substring(0, names.length - 2).ansi(ANSI_GREEN))
    }
}
