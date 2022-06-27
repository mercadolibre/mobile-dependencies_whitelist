package com.mercadolibre.android.gradle.library.integration.plugins_tests

import com.mercadolibre.android.gradle.library.integration.utils.TaskTest
import org.gradle.api.Project

object PluginLibraryTest: TaskTest() {

    fun publishTasks(project: Project) {
        assert(findExtension("publishing", project))
        assert(project.tasks.findByName("publishAllPublicationsToAndroidInternalExperimentalRepository") != null)
        assert(project.tasks.findByName("publishAllPublicationsToAndroidInternalReleasesRepository") != null)
        assert(project.tasks.findByName("publishAllPublicationsToAndroidPublicReleasesRepository") != null)
        assert(project.tasks.findByName("publishToMavenLocal") != null)
        assert(project.tasks.findByName("publish") != null)
    }

}
