package com.mercadolibre.android.gradle.baseplugin.unitary.modules.jacoco

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.JavaJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics.JacocoConfigurationExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.FileManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class JacocoTest : AbstractPluginManager() {

    val jacocoModule = JavaJacocoModule()
    val libraryJacocoModule = LibraryJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(
            ROOT_PROJECT,
            mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, ANY_NAME to ModuleType.LIBRARY),
            projects,
            fileManager
        )

        JacocoConfigurationExtension().excludeList = listOf()

        jacocoModule.createNeededTasks(projects[LIBRARY_PROJECT]!!)

        libraryJacocoModule.findOrCreateJacocoTestReportTask(projects[ANY_NAME]!!)

        projects[LIBRARY_PROJECT]!!.tasks.create("testAnyNameUnitTest", Test::class.java)
    }

    @org.junit.Test
    fun `When the AndroidJacocoModule is called find or create Test Report Task and not exist`() {
        libraryJacocoModule.findOrCreateJacocoTestReportTask(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AndroidJacocoModule is called find or create Test Report Task`() {
        libraryJacocoModule.findOrCreateJacocoTestReportTask(projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the AndroidJacocoModule is called configure the project`() {
        val variant = mockVariant()
        val report = mockk<JacocoReport>(relaxed = true)
        val test = mockk<Test>()
        val sourceProvider = mockk<SourceProvider>()
        val file = mockk<File>()

        every { file.path } returns ANY_NAME
        every { sourceProvider.javaDirectories } returns listOf(file)
        every { variant.sourceSets } returns mutableListOf(sourceProvider)

        every { test.extensions } returns mockk(relaxed = true)

        every { libraryJacocoModule.executionDataFile(test) } returns "/"

        libraryJacocoModule.configureReport(report, test, variant, projects[LIBRARY_PROJECT]!!)
    }

    @org.junit.Test
    fun `When the JavaJacocoModule is called configure the project`() {
        jacocoModule.configure(projects[LIBRARY_PROJECT]!!)
        jacocoModule.configureProejct(projects[LIBRARY_PROJECT]!!)
        jacocoModule.configureTestReport(mockk(relaxed = true))
        assert(projects[LIBRARY_PROJECT]!!.tasks.names.contains(JACOCO_FULL_REPORT_TASK))
        assert(projects[LIBRARY_PROJECT]!!.tasks.names.contains(JACOCO_TEST_REPORT_TASK))
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule is called configure the project`() {
        val variant = mockk<BaseVariant>()

        every { variant.name } returns ANY_NAME
        every { variant.sourceSets } returns mockk(relaxed = true)

        mockkObject(VariantUtils)

        every { VariantUtils.javaCompile(variant).destinationDirectory.asFile.orNull } returns mockk(relaxed = true)

        libraryJacocoModule.createReportTask(variant, projects[LIBRARY_PROJECT]!!)
    }
}
