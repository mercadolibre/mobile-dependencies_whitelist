package com.mercadolibre.android.gradle.app.unitary.configurers

import com.mercadolibre.android.gradle.app.core.action.configurers.AppBasicsConfigurer
import com.mercadolibre.android.gradle.app.managers.ANY_NAME
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolveDetails
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BasicsConfigurerTest : AbstractPluginManager() {

    private val basicsConfigurer = AppBasicsConfigurer()
    @org.junit.Test
    fun `When the AppBasicsConfigurer is called provide the description`() {
        assert(
            basicsConfigurer.getDescription() ==
                "This configurer is in charge of forcing the dependencies so that the project does not generate repeated outputs."
        )
    }

    @org.junit.Test
    fun `When the AppBasicsConfigurer is called configure the dependencies`() {
        val project = mockk<Project>(relaxed = true)
        val dependencyResolveDetails = mockk<DependencyResolveDetails>(relaxed = true) {
            every { requested.group } returns "org.ow2.asm"
        }
        val dependencyResolveDetails2 = mockk<DependencyResolveDetails>(relaxed = true) {
            every { requested.group } returns ANY_NAME
        }

        basicsConfigurer.configureProject(project)
        basicsConfigurer.setConfigurations(project)
        basicsConfigurer.configResolutionStrategy(mockk(relaxed = true))
        basicsConfigurer.configureDependencies(dependencyResolveDetails)
        basicsConfigurer.configureDependencies(dependencyResolveDetails2)

        verify { dependencyResolveDetails.requested }
    }
}
