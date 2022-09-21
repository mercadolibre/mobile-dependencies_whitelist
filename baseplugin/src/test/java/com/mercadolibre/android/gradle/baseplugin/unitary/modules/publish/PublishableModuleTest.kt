package com.mercadolibre.android.gradle.baseplugin.unitary.modules.publish

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.JavaPublishableModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.TaskGenerator
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics.VersionContainer
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
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_1
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PublishableModuleTest : AbstractPluginManager() {

    private val javaPublishableModule = JavaPublishableModule()

    private lateinit var variant: BaseVariant

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        variant = mockVariant()

        javaPublishableModule.configure(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the JavaPublishableModule is called then configure all sourceset`() {
        val project = mockk<Project>(relaxed = true)
        val extension = mockk<SourceSetContainer>(relaxed = true)
        val publishExtension = mockk<PublishingExtension>(relaxed = true)

        every { project.configurations.findByName("archives") } returns mockk(relaxed = true)
        every { project.configurations.findByName("default") } returns mockk(relaxed = true)
        every { project.extensions.findByType(SourceSetContainer::class.java) } returns extension
        every { project.extensions.findByType(PublishingExtension::class.java) } returns publishExtension

        javaPublishableModule.configure(project)

        // Project find the extension
        verify { project.extensions.findByType(SourceSetContainer::class.java) }

        // Module configure the repositories to publish
        verify { publishExtension.publications(any<Action<PublicationContainer>>()) }

        // Project configure all Source Set in SourceSetContainer extension
        verify { extension.all(any<Action<SourceSet>>()) }
    }

    @org.junit.Test
    fun `When any PublishAarTask is called and register then return a task`() {
        val variant = mockLibVariant()
        val sourceSet = mockk<SourceProvider>() {
            every { javaDirectories } returns listOf(mockk(relaxed = true))
        }

        every { variant.sourceSets } returns listOf(sourceSet)

        val task = PublishAarExperimentalTask().register(projects[LIBRARY_PROJECT]!!, variant, ANY_NAME)

        assert(task is TaskProvider<Task>)
    }

    @org.junit.Test
    fun `When the TaskGenerator is created make a task and her own version`() {
        val project = mockk<Project>(relaxed = true)
        val versionContainer = mockk<VersionContainer>(relaxed = true)

        every { project.name } returns LIBRARY_PROJECT
        every { project.version } returns VERSION_1

        val taskGenerator = TaskGenerator(ANY_NAME, VERSION_1, versionContainer, listOf(), ANY_NAME, project)
        taskGenerator.logVersion(ANY_NAME)

        verify { versionContainer.put(LIBRARY_PROJECT, ANY_NAME, VERSION_1) }

        verify { project.tasks.register(ANY_NAME) }
    }

    @org.junit.Test
    fun `When the TaskGenerator is created dont create again when task is already created`() {
        val project = mockk<Project>(relaxed = true)
        val versionContainer = mockk<VersionContainer>(relaxed = true)

        every { project.name } returns LIBRARY_PROJECT
        every { project.version } returns VERSION_1

        every { project.tasks.names.contains(ANY_NAME) } returns true

        val taskGenerator = TaskGenerator(ANY_NAME, VERSION_1, versionContainer, listOf(), ANY_NAME, project)
        taskGenerator.logVersion(ANY_NAME)

        verify { versionContainer.put(LIBRARY_PROJECT, ANY_NAME, VERSION_1) }

        verify { project.tasks.register(ANY_NAME) wasNot Called }
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
