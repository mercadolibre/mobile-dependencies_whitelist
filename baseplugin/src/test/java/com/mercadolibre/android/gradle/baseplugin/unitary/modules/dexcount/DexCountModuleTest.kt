package com.mercadolibre.android.gradle.baseplugin.unitary.modules.dexcount

import com.getkeepsafe.dexcount.DexCountExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.dexcount.DexCountModule
import com.mercadolibre.android.gradle.baseplugin.core.components.DEXCOUNT_PROPERTY
import com.mercadolibre.android.gradle.baseplugin.core.components.JSON_CONSTANT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project

class DexCountModuleTest {

    private val dexCountModule = DexCountModule()

    @org.junit.Test
    fun `When the DexCountModule is called configure the project`() {
        val extension = mockk<DexCountExtension>(relaxed = true)

        val project = mockk<Project>(relaxed = true) {
            every { hasProperty(DEXCOUNT_PROPERTY) } returns true
            every { extensions.findByType(DexCountExtension::class.java) } returns extension
        }

        dexCountModule.configure(project)

        verify { extension.format = JSON_CONSTANT }
        verify { extension.includeClassCount = true }
        verify { extension.includeFieldCount = true }
        verify { extension.includeTotalMethodCount = true }
    }

}