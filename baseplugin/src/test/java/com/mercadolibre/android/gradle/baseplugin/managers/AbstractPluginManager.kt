package com.mercadolibre.android.gradle.baseplugin.managers

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

abstract class AbstractPluginManager : ExtensionGetter() {

    lateinit var root: Project
    lateinit var mockedRoot: MockedRootProject

    lateinit var tmpFolder: TemporaryFolder

    val projects: MutableMap<String, Project> = mutableMapOf()

    val moduleManager = ModuleManager()

    val pathsAffectingAllModules = setOf(
        "tools/android/buildSrc",
        "android/gradlew",
        "android/gradle"
    )

    fun initTmpFolder() {
        tmpFolder = TemporaryFolder()
        tmpFolder.create()
    }

    private fun createMockProject(name: String): MockedProjectContent {
        val project = mockk<Project>()
        val extensions = mockk<ExtensionContainer>()
        val tasks = mockk<TaskContainer>()
        val configuration = mockk<Configuration>()

        val mapOfExtensions = mutableMapOf<String, Any>().apply {

            val baseExtensionMocked = mockk<BaseExtension>(relaxed = true)

            val libraryExtensionMocked = mockk<LibraryExtension>(relaxed = true) {
                every { buildTypes.iterator().next() } returns mockk(relaxed = true)
            }
            val appExtensionMocked = mockk<AppExtension>(relaxed = true) {
                every { buildTypes.iterator().next() } returns mockk(relaxed = true)
            }

            put(BaseExtension::class.java.simpleName, baseExtensionMocked)
            put(LibraryExtension::class.java.simpleName, libraryExtensionMocked)
            put(AppExtension::class.java.simpleName, appExtensionMocked)

            every { extensions.findByType(LibraryExtension::class.java) } returns libraryExtensionMocked
            every { extensions.findByType(BaseExtension::class.java) } returns baseExtensionMocked
            every { extensions.findByType(AppExtension::class.java) } returns appExtensionMocked
        }

        val mockedProject = MockedProjectContent(project, extensions, mapOfExtensions, arrayListOf(), tasks, arrayListOf())

        mockedProject.variants.add(mockVariant())
        mockedProject.configurations.add(configuration)

        every { project.name } returns name
        every { project.tasks } returns tasks
        every { project.version } returns VERSION_1
        every { tasks.names } returns mockk(relaxed = true)
        every { project.extensions } returns extensions

        val sampleTaskProvider = mockk<TaskProvider<Task>>()

        every { sampleTaskProvider.get() } returns mockk<Task>(relaxed = true)

        return mockedProject
    }

    inline fun <reified T> getMockedExtension(mockedProject: MockedProjectContent) =
        mockedProject.extensions[T::class.java.simpleName]!! as T

    private fun mockSubProject(name: String, root: Project): MockedProjectContent {
        val subProject = createMockProject(name)

        every { subProject.project.rootProject } returns root

        return subProject
    }

    fun mockRootProject(subProjectsList: List<String>): MockedRootProject {
        val project = createMockProject(ROOT_PROJECT)
        val mockedRootProject = MockedRootProject(project, mutableMapOf())

        val subProjects = mutableSetOf<Project>()

        for (subProject in subProjectsList) {
            val mockedSubProject = mockSubProject(subProject, project.project)
            mockedRootProject.subProjects[subProject] = mockedSubProject
            subProjects.add(mockedSubProject.project)
        }

        every { project.project.subprojects } returns subProjects
        return mockedRootProject
    }

    fun addMockVariant(project: Project, type: ModuleType) {
        PluginConfigurer(if (type == ModuleType.LIBRARY) { LIBRARY_PLUGINS } else { APP_PLUGINS }).configureProject(project)
        project.configurations.create(ANY_CONFIGURATION)
        if (type == ModuleType.LIBRARY) {
            findExtension<LibraryExtension>(projects[LIBRARY_PROJECT]!!)?.apply {
                libraryVariants.add(mockLibVariant())
            }
        } else {
            findExtension<AppExtension>(projects[APP_PROJECT]!!)?.apply {
                applicationVariants.add(mockAppVariant())
            }
        }
    }

    fun mockLibVariant(): LibraryVariant = mockk<LibraryVariant>().apply { configVariant(this) }

    fun mockVariant(): BaseVariant = mockk<BaseVariant>().apply { configVariant(this) }

    fun mockSourceSet(): SourceSet = mockk<SourceSet>().apply { configSourceSet(this) }

    fun mockAppVariant(): ApplicationVariant = mockk<ApplicationVariant>().apply { configVariant(this) }

    private fun configSourceSet(sourceSet: SourceSet) {
        every { sourceSet.name } returns "AnyProductFlavoranyName"
    }

    private fun configVariant(variant: BaseVariant) {
        val configuration = mockk<Configuration>()

        every { variant.name } returns "AnyProductFlavoranyName"
        every { variant.buildType } returns mockk(relaxed = true)
        every { variant.sourceSets } returns mockk(relaxed = true)
        every { variant.flavorName } returns ANY_FLAVOR
        every { variant.compileConfiguration } returns configuration
        every { variant.runtimeConfiguration } returns configuration
        every { configuration.attributes } returns mockk(relaxed = true)
        every { configuration.name } returns ANY_CONFIGURATION

        mockkObject(VariantUtils)

        every { VariantUtils.javaCompile(variant).source } returns mockk()
        every { VariantUtils.packageLibrary(variant) } returns ANY_PATH
        every { VariantUtils.javaCompile(variant).destinationDirectory.asFile.orNull } returns mockk()
    }

    fun getOutputOfGradle(task: String, folder: File): BuildResult =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments(task)
            .withProjectDir(folder)
            .build()

    fun runGradle(folder: File): BuildResult =
        GradleRunner.create()
            .withProjectDir(folder)
            .withPluginClasspath()
            .build()
}
