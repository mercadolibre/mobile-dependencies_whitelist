
package com.mercadolibre.android.gradle.app.unitary.modules.lint

import com.mercadolibre.android.gradle.app.core.action.modules.lint.ReleaseDependenciesLint
import com.mercadolibre.android.gradle.app.managers.ANY_GROUP
import com.mercadolibre.android.gradle.app.managers.ANY_NAME
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.managers.VERSION_1
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.APP_PLUGINS
import com.mercadolibre.android.gradle.baseplugin.core.components.LINTABLE_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_EXPERIMENTAL
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.artifacts.Dependency
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class ReleaseDependenciesTest : AbstractPluginManager() {

    val releaseDependencies = ReleaseDependenciesLint()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        PluginConfigurer(APP_PLUGINS).configureProject(projects[APP_PROJECT]!!)
        projects[APP_PROJECT]!!.extensions.create(LINTABLE_EXTENSION, LintGradleExtension::class.java)

        releaseDependencies.name()

        val configuration = projects[APP_PROJECT]!!.configurations.create(ANY_NAME)

        val dependency = mockk<Dependency>()

        every { dependency.name } returns ANY_NAME
        every { dependency.group } returns ANY_GROUP
        every { dependency.version } returns PUBLISHING_EXPERIMENTAL

        every { dependency.version } returns VERSION_1

        configuration.dependencies.add(dependency)

        findExtension<LintGradleExtension>(projects[APP_PROJECT]!!)?.apply {
            this.enabled = true
            this.dependenciesLintEnabled = true
            this.releaseDependenciesLintEnabled = true
        }
    }

    @org.junit.Test
    fun `When the ReleaseDependenciesLint lint excute right`() {
        val variant = mockVariant()

        releaseDependencies.lint(projects[APP_PROJECT]!!, arrayListOf(variant))
    }

    @org.junit.Test
    fun `When the ReleaseDependenciesLint is called check is exist any fail`() {
        val file = mockk<File>(relaxed = true)

        every { file.parentFile.mkdirs() } returns mockk(relaxed = true)
        every { file.exists() } returns mockk(relaxed = true)
        every { file.path } returns "./asd.txt"

        releaseDependencies.checkIsFailed(listOf(ANY_NAME).stream(), file)
        File("./asd.txt").delete()
    }
}
