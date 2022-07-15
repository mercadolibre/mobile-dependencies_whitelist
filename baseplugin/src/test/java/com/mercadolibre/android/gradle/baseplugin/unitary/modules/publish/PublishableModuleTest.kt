package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.JavaPublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarExperimentalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarLocalTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarPrivateReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.subClasses.PublishAarPublicReleaseTask
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.SOURCE_SETS_DEFAULT
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.gradle.api.tasks.SourceSet
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class PublishableModuleTest : AbstractPluginManager() {

    private val javaPublishableModule = JavaPublishableModule()

    lateinit var variant: BaseVariant

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        javaPublishableModule.configure(projects[LIBRARY_PROJECT]!!)

        variant = mockVariant()
    }

    @org.junit.After
    fun after() {
        unmockkAll()
    }

    @org.junit.Test
    fun `When the TaskGenerator is created works fine`() {
        val taskGenerator = TaskGenerator(ANY_NAME, ANY_NAME, mockk(relaxed = true), listOf(), ANY_NAME, projects[LIBRARY_PROJECT]!!)
        taskGenerator.logVersion(ANY_NAME)
    }

    @org.junit.Test
    fun `When the TaskGenerator is created works fine when task is already created`() {
        projects[LIBRARY_PROJECT]!!.tasks.create(ANY_NAME)
        val taskGenerator = TaskGenerator(ANY_NAME, ANY_NAME, mockk(relaxed = true), listOf(), ANY_NAME, projects[LIBRARY_PROJECT]!!)
        taskGenerator.logVersion(ANY_NAME)
    }

    @org.junit.Test
    fun `When the PublishAarLocalTask register the project create her task`() {
        val task = PublishAarLocalTask()

        task.register(projects[LIBRARY_PROJECT]!!, variant, "PublishAarLocalTask")

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("PublishAarLocalTask") != null)
    }

    @org.junit.Test
    fun `When the PublishAarExperimentalTask register the project create her task`() {
        val task = PublishAarExperimentalTask()

        task.register(projects[LIBRARY_PROJECT]!!, variant, "PublishAarExperimentalTask")

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("PublishAarExperimentalTask") != null)
    }

    @org.junit.Test
    fun `When the PublishAarPublicReleaseTask register the project create her task`() {
        val task = PublishAarPublicReleaseTask()
        val task2 = PublishAarPrivateReleaseTask()

        task.register(projects[LIBRARY_PROJECT]!!, variant, "PublishAarPublicReleaseTask")
        task2.register(projects[LIBRARY_PROJECT]!!, variant, "PublishAarPrivateReleaseTask")

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("PublishAarPrivateReleaseTask") != null)
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("PublishAarPublicReleaseTask") != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create add Tasks Default`() {
        val variant = mockk<SourceSet>()

        every { variant.name } returns SOURCE_SETS_DEFAULT
        every { variant.allSource } returns mockk(relaxed = true)

        javaPublishableModule.addTask(projects[LIBRARY_PROJECT]!!, variant)

        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishRelease") != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create publish Task`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName(PUBLISH_CONSTANT) != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create publish Task to Maven Local`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishToMavenLocal") != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create publish Task to AndroidInternalExperimental Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidInternalExperimentalRepository") != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create publish Task to AndroidInternalReleases Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidInternalReleasesRepository") != null)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule configures the project create publish Task to AndroidPublicReleases Repository`() {
        assert(projects[LIBRARY_PROJECT]!!.tasks.findByName("publishAllPublicationsToAndroidPublicReleasesRepository") != null)
    }
}
