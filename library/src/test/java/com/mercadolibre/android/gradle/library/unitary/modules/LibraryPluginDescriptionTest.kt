package com.mercadolibre.android.gradle.library.unitary.modules

import com.mercadolibre.android.gradle.library.core.action.configurers.LibraryModuleConfigurer
import com.mercadolibre.android.gradle.library.core.action.modules.pluginDescription.LibraryPluginDescriptionModule
import com.mercadolibre.android.gradle.library.managers.ANY_NAME
import com.mercadolibre.android.gradle.library.module.ModuleProvider
import io.mockk.mockk
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryPluginDescriptionTest {

    @org.junit.Test
    fun `When the LibraryPluginDescriptionModule is created works`() {
        val pluginDescription = LibraryPluginDescriptionModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.configureTask(task)
        assert(pluginDescription.makeMessage(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")
        assert(
            pluginDescription.content() ==
                LibraryModuleConfigurer().getModules("Library Module", ModuleProvider.provideLibraryAndroidModules())
        )
    }
}
