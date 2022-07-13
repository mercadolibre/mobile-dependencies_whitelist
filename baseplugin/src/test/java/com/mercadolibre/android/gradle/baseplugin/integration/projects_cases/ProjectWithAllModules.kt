package com.mercadolibre.android.gradle.baseplugin.integration.projects_cases

import com.mercadolibre.android.gradle.baseplugin.integration.utils.UtilsTest
import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import com.mercadolibre.android.gradle.baseplugin.managers.APP_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.LIBRARY_PROJECT
import com.mercadolibre.android.gradle.baseplugin.managers.TEST_APP_PROJECT
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectWithAllModules: UtilsTest() {

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
        return mutableMapOf(LIBRARY_PROJECT to ModuleType.LIBRARY, TEST_APP_PROJECT to ModuleType.TESTAPP, APP_PROJECT to ModuleType.APP)
    }

}