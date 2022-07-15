package com.mercadolibre.android.gradle.baseplugin.unitary.configurers

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_EXTRA
import com.mercadolibre.android.gradle.baseplugin.core.components.EXTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_EXPERIMENTAL
import com.mercadolibre.android.gradle.baseplugin.core.components.INTERNAL_RELEASES
import com.mercadolibre.android.gradle.baseplugin.managers.AbstractPluginManager
import com.mercadolibre.android.gradle.baseplugin.managers.ROOT_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BasicsConfigurerTest : AbstractPluginManager() {

    val basePlugin = BasePlugin()

    val basicsConfigurer = BasicsConfigurer()

    @org.junit.Before
    fun setUp() {
        initTmpFolder()
        root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
        basicsConfigurer.configureProject(root)
    }

    @org.junit.Test
    fun `When the BasicsConfigurer configures a project create the repository AndroidInternalExperimental`() {
        assert(root.repositories.findByName(INTERNAL_EXPERIMENTAL) != null)
    }

    @org.junit.Test
    fun `When the BasicsConfigurer configures a project create the repository AndroidInternalReleases`() {
        assert(root.repositories.findByName(INTERNAL_RELEASES) != null)
    }

    @org.junit.Test
    fun `When the BasicsConfigurer configures a project create the repository AndroidExternalReleases`() {
        assert(root.repositories.findByName(EXTERNAL_RELEASES) != null)
    }

    @org.junit.Test
    fun `When the BasicsConfigurer configures a project create the repository AndroidExtra`() {
        assert(root.repositories.findByName(ANDROID_EXTRA) != null)
    }

    @org.junit.Test
    fun `When the BasicsConfigurer configures a project create the repository MavenLocal`() {
        assert(root.repositories.findByName("MavenLocal") != null)
    }
}
