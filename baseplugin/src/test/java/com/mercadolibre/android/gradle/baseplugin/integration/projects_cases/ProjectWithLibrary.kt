package com.mercadolibre.android.gradle.baseplugin.integration.projects_cases

import com.mercadolibre.android.gradle.baseplugin.integration.utils.UtilsTest
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectWithLibrary: UtilsTest() {
    /*
        En este test de integracion se generara un proyecto root con un modulo de tipo Libreria

               root-2
                  |
                  p1

              p1 --> Library
              p2 --> App
              p3 --> Test App
         */

    override fun getProjectsName(): MutableMap<String, ModuleType> {
        return mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY)
    }

}