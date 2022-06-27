package com.mercadolibre.android.gradle.library.unitary.modules

import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RELEASE_CONSTANT
import com.mercadolibre.android.gradle.library.core.action.modules.publishable.LibraryPublishableModule
import com.mercadolibre.android.gradle.library.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.library.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.library.managers.FileManager
import com.mercadolibre.android.gradle.library.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.library.managers.ROOT_PROJECT
import io.mockk.every
import java.io.File
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LibraryPublishableModuleTest: AbstractPluginManager() {

    val publishableModule = LibraryPublishableModule()

    lateinit var variant: LibraryVariant

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY), projects, fileManager)

        publishableModule.configure(projects[LIBRARY_PROJECT]!!)

        variant = mockLibVariant()
    }

    @org.junit.Test
    fun `When the LibraryPublishableModule is called create tasks`() {

        every { variant.name } returns RELEASE_CONSTANT

        publishableModule.createTasksFor(variant, projects[LIBRARY_PROJECT]!!)
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