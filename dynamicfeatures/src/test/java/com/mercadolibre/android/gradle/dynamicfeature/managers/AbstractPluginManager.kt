package com.mercadolibre.android.gradle.dynamicfeature.managers

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.core.components.LIBRARY_PLUGINS
import com.mercadolibre.android.gradle.dynamicfeature.utils.domain.ModuleType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

abstract class AbstractPluginManager : ExtensionGetter() {

    lateinit var root: Project

    lateinit var tmpFolder: TemporaryFolder
    lateinit var tmpDir: File

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
        tmpDir = tmpFolder.root
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

    fun mockAppVariant(): ApplicationVariant = mockk<ApplicationVariant>().apply { configVariant(this) }

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

    fun runGradle(folder: File): BuildResult =
        GradleRunner.create()
            .withProjectDir(folder)
            .withPluginClasspath()
            .build()
}
