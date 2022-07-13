package com.mercadolibre.android.gradle.library.unitary.modules

import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RELEASE_CONSTANT
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.managers.ANY_NAME
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.utils.domain.ModuleType
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryPublishableModuleTest: AbstractPluginManager() {

    private val publishableModule = LibraryPublishableModule()

    lateinit var variant: LibraryVariant

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        addMockVariant(projects[LIBRARY_PROJECT]!!, ModuleType.LIBRARY)

        publishableModule.configure(projects[LIBRARY_PROJECT]!!)

        variant = mockLibVariant()
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule is called create tasks`() {

        every { variant.name } returns RELEASE_CONSTANT

        publishableModule.createTasksFor(variant, projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule is called create a sub task`() {

        every { variant.name } returns RELEASE_CONSTANT
        projects[LIBRARY_PROJECT]!!.tasks.create(ANY_NAME)
        publishableModule.createStubTask(ANY_NAME, mockk(relaxed = true), projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule configures the project create publish Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(PUBLISH_CONSTANT) != null)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule configures the project create publish Task to Maven Local`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishToMavenLocal") != null)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule configures the project create publish Task to AndroidInternalExperimental Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidInternalExperimentalRepository") != null)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule configures the project create publish Task to AndroidInternalReleases Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidInternalReleasesRepository") != null)
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule configures the project create publish Task to AndroidPublicReleases Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidPublicReleasesRepository") != null)
    }
}