package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.configurers.AppModuleConfigurer
import com.mercadolibre.android.gradle.app.core.action.modules.pluginDescription.AppPluginDescriptionModule
import com.mercadolibre.android.gradle.app.managers.ANY_NAME
import com.mercadolibre.android.gradle.app.module.ModuleProvider
import io.mockk.mockk
import org.gradle.api.Task
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppPluginDescriptionTest {

    @org.junit.Test
    fun `When the AppPluginDescriptionModule is created workd`() {
        val pluginDescription = AppPluginDescriptionModule()

        val task = mockk<Task>(relaxed = true)

        pluginDescription.printMessage(ANY_NAME)
        pluginDescription.configureTask(task)
        assert(pluginDescription.makeMessage(ANY_NAME, ANY_NAME) == "- $ANY_NAME\n$ANY_NAME")
        assert(pluginDescription.content() == AppModuleConfigurer().getModules("App Modules", ModuleProvider.provideAppAndroidModules()))
    }
}
