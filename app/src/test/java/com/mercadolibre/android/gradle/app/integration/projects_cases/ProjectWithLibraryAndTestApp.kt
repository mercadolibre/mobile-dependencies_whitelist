package com.mercadolibre.android.gradle.app.integration.projects_cases

import com.mercadolibre.android.gradle.app.integration.utils.UtilsTest
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.app.managers.TEST_APP_PROJECT
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectWithLibraryAndTestApp: UtilsTest() {

    /*
        En este test de integracion se generara un proyecto root con un modulo de tipo App y otro de tipo Test App

           root-1
           /    \
          p1    p3

          p1 --> Library
          p2 --> App
          p3 --> Test App
     */

    override fun getProjectsName(): MutableMap<String, ModuleType> {
        return mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, TEST_APP_PROJECT to ModuleType.APP)
    }

}