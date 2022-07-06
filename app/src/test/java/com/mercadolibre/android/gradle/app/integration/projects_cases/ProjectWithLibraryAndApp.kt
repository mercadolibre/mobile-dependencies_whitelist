package com.mercadolibre.android.gradle.app.integration.projects_cases

import com.mercadolibre.android.gradle.app.integration.utils.UtilsTest
import com.mercadolibre.android.gradle.app.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.app.managers.APP_PROJECT
import com.mercadolibre.android.gradle.app.managers.LIBRARY_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectWithLibraryAndApp: UtilsTest() {

    /*
        En este test de integracion se generara un proyecto root con un modulo de tipo App y otro de tipo App

           root-0
           /    \
          p1    p2

          p1 --> Library
          p2 --> App
          p3 --> Test App
     */

    override fun getProjectsName(): MutableMap<String, ModuleType> {
        return mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, APP_PROJECT to ModuleType.APP)
    }

}