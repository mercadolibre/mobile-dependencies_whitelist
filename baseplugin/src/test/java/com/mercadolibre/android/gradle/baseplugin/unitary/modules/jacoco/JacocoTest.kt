package com.mercadolibre.android.gradle.baseplugin.unitary.modules.jacoco

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.JavaJacocoModule
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.jacoco.basics.JacocoConfigurationExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.VariantUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_FULL_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_GROUP
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.JACOCO_TEST_REPORT_TASK
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.library.core.action.modules.jacoco.LibraryJacocoModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class JacocoTest : AbstractPluginManager() {

    private val jacocoModule = JavaJacocoModule()
    private val libraryJacocoModule = LibraryJacocoModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        mockedRoot = mockRootProject(mutableListOf(LIBRARY_PROJECT))

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[LIBRARY_PROJECT] = moduleManager.createSampleSubProject(LIBRARY_PROJECT, tmpFolder, root)

        jacocoModule.executeInAfterEvaluate()

        JacocoConfigurationExtension().excludeList = listOf()

        jacocoModule.createNeededTasks(projects[LIBRARY_PROJECT]!!)

        projects[LIBRARY_PROJECT]!!.tasks.create("testAnyNameUnitTest", Test::class.java)
    }

    @org.junit.Test
    fun `When the JavaJacocoModule is called before evaluate execute her configuration`() {
        val project = mockk<Project>(relaxed = true)

        every { project.tasks.register(JACOCO_FULL_REPORT_TASK).get() } returns mockk(relaxed = true)

        jacocoModule.moduleConfiguration(project)

        verify { project.tasks.register(JACOCO_FULL_REPORT_TASK) }
    }

    @org.junit.Test
    fun `When the AndroidJacocoModule is called then find or create test report task`() {
        val project = mockk<Project>(relaxed = true)

        val task = libraryJacocoModule.findOrCreateJacocoTestReportTask(project)

        assert(libraryJacocoModule.getExtensionName() == JACOCO_EXTENSION)
        assert(task === libraryJacocoModule.findOrCreateJacocoTestReportTask(project))
    }

    @org.junit.Test
    fun `When the AndroidJacocoModule is called then configure the reports`() {
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

        verify { report.group = JACOCO_GROUP }
        verify { report.description = "$JACOCO_TEST_REPORT_DESCRIPTION for the AnyProductFlavoranyName variant." }
        verify { report.executionData.from(any()) }

        verify { report.sourceDirectories.from(any()) }
        verify { report.classDirectories.from(any()) }
        verify { report.executionData.from(any()) }

        verify { report.reports(any<Action<JacocoReportsContainer>>()) }
    }

    @org.junit.Test
    fun `When the JavaJacocoModule is called then create all necessary task`() {
        val project = mockk<Project>(relaxed = true)

        jacocoModule.configure(projects[LIBRARY_PROJECT]!!)
        jacocoModule.configureProject(project)
        jacocoModule.configureTestReport(mockk(relaxed = true))

        assert(projects[LIBRARY_PROJECT]!!.tasks.names.contains(JACOCO_FULL_REPORT_TASK))
        assert(projects[LIBRARY_PROJECT]!!.tasks.names.contains(JACOCO_TEST_REPORT_TASK))
    }

    @org.junit.Test
    fun `When the LibraryJacocoModule is called create the jacoco report task`() {
        val variant = mockk<BaseVariant>()

        every { variant.name } returns ANY_NAME
        every { variant.sourceSets } returns mockk(relaxed = true)

        mockkObject(VariantUtils)

        every { VariantUtils.javaCompile(variant).destinationDirectory.asFile.orNull } returns mockk(relaxed = true)

        val task = libraryJacocoModule.createReportTask(variant, projects[LIBRARY_PROJECT]!!)

        // Create task name with variant.name
        verify { variant.name }

        // Task created
        assert(task is TaskProvider<JacocoReport>)
    }
}
