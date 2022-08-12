package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.modules.keystore.KeyStoreExtension
import com.mercadolibre.android.gradle.app.core.action.modules.keystore.KeyStoreModule
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.app.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.core.components.UNPACK_DEBUG_KEY_STORE_TASK
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class KeyStoreModuleTest : AbstractPluginManager() {

    private val keyStoreModule = KeyStoreModule()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)

        keyStoreModule.createExtension(projects[APP_PROJECT]!!)

        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project write the keystore file`() {
        val project = mockk<Project>()
        val file = File("./build/tmp/asd.txt")
        file.createNewFile()

        every { project.file("./build/tmp") } returns file

        keyStoreModule.writeFile(project, "./build/tmp", file)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive does nothing`() {

        projects[APP_PROJECT]!!.extensions.findByType(KeyStoreExtension::class.java)?.apply {
            enabled = false
        }

        keyStoreModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) == null)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive execute config`() {
        projects[APP_PROJECT]!!.tasks.register("validateSigningAnyProductFlavoranyName")

        projects[APP_PROJECT]!!.extensions.findByType(KeyStoreExtension::class.java)?.apply {
            enabled = true
        }

        keyStoreModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) != null)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive execute config without validate task`() {

        projects[APP_PROJECT]!!.extensions.findByType(KeyStoreExtension::class.java)?.apply {
            enabled = true
        }
        keyStoreModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) != null)
    }
}
