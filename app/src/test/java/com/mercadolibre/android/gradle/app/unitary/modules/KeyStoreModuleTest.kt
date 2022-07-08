package com.mercadolibre.android.gradle.app.unitary.modules

import com.mercadolibre.android.gradle.app.core.action.modules.keystore.KeyStoreModule
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.app.managers.FileManager
import com.mercadolibre.android.gradle.app.managers.ROOT_PROJECT
import com.mercadolibre.android.gradle.baseplugin.core.components.UNPACK_DEBUG_KEY_STORE_TASK
import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.gradle.api.Project
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class KeyStoreModuleTest: AbstractPluginManager() {

    val keyStoreModule = KeyStoreModule(false)
    val keyStoreProductiveModule = KeyStoreModule(true)

    @org.junit.Before
    fun setUp() {
        initTmpFolder()

        val fileManager = FileManager(tmpFolder)

        pathsAffectingAllModules.forEach { File(tmpFolder.root, it).mkdirs() }

        root = moduleManager.createRootProject(ROOT_PROJECT, mutableMapOf(APP_PROJECT to ModuleType.APP), projects, fileManager)

        addMockVariant(projects[APP_PROJECT]!!, ModuleType.APP)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project write the keystore file`() {
        val project = mockk<Project>()
        val file = File("./asd.txt")
        file.createNewFile()

        every { project.file("./") } returns file

        keyStoreModule.writeFile(project, "./", file)
        file.delete()
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive does nothing`() {
        keyStoreModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) == null)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive execute config`() {
        projects[APP_PROJECT]!!.tasks.register("validateSigningAnyProductFlavoranyName")
        keyStoreProductiveModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) != null)
    }

    @org.junit.Test
    fun `When the KeyStoreModule configures the project not productive execute config without validate task`() {
        keyStoreProductiveModule.configure(projects[APP_PROJECT]!!)
        assert(projects[APP_PROJECT]!!.tasks.findByName(UNPACK_DEBUG_KEY_STORE_TASK) != null)
    }

}