package com.mercadolibre.android.gradle.baseplugin.managers

import com.mercadolibre.android.gradle.baseplugin.integration.utils.domain.ModuleType
import org.gradle.api.Project
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.provider.Provider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.internal.service.DefaultServiceRegistry
import org.gradle.internal.service.scopes.ProjectScopeServices
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.events.OperationCompletionListener
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicReference

class ModuleManager {

    fun createSampleRoot(name: String, tmpFolder: TemporaryFolder): Project {
        val root = ProjectBuilder.builder().withProjectDir(tmpFolder.root).withName(name).build()
        (root.properties["ext"] as ExtraPropertiesExtension).set("supportRootFolder", tmpFolder)
        return root
    }

    fun createRootProject(
        name: String,
        projectNames: MutableMap<String, ModuleType>,
        projects: MutableMap<String, Project>,
        fileManager: FileManager
    ): Project {
        val root = createSampleRoot(name, fileManager.tmpFolder)

        val include = getSubProjects(root, projectNames, projects, fileManager)

        fileManager.createFile("repositories.gradle", fileManager.readFile("gradle/repositories.gradle"))
        fileManager.createFile("gradle.properties", fileManager.readFile("gradle.properties"))
        fileManager.createFile("local.properties", fileManager.readFile("templates/localproperties"))
        fileManager.createFile(
            "build.gradle",
            fileManager.readFile("templates/rootBuildGradle.gradle")
                .replace("tmpFolder", "${fileManager.tmpFolder.root}")
        )
        fileManager.createFile(
            "settings.gradle",
            fileManager.readFile("templates/rootSettingsGradle.gradle")
                .replace("tmpFolder", "${fileManager.tmpFolder.root}") + "\ninclude $include"
        )

        return root
    }

    private fun getSubProjects(
        root: Project,
        projectNames: MutableMap<String, ModuleType>,
        projects: MutableMap<String, Project>,
        fileManager: FileManager
    ): String {
        var include = ""
        for (project in projectNames) {
            projects[project.key] = createSubProject(project.key, project.value, root, fileManager, project.value == ModuleType.APP)
            include += "':${project.key}', "
        }
        return include.substring(IntRange(0, include.length - 3))
    }

    fun createSampleSubProject(name: String, tmpFolder: TemporaryFolder, root: Project): Project {
        val tmpProjectFolder = tmpFolder.newFolder(name)
        val project = ProjectBuilder.builder().withProjectDir(tmpProjectFolder).withName(name).withParent(root).build()
        configModule(project)
        return project
    }

    fun createSubProject(name: String, type: ModuleType, root: Project, fileManager: FileManager, productive: Boolean): Project {
        val project = createSampleSubProject(name, fileManager.tmpFolder, root)

        makeBuildGradle(name, type, fileManager.tmpFolder, fileManager, productive)

        return project
    }

    private fun makeBuildGradle(name: String, type: ModuleType, tmpFolder: TemporaryFolder, fileManager: FileManager, productive: Boolean) {
        val plugin =
            if (type == ModuleType.APP) {
                "'mercadolibre.gradle.config.app'"
            } else {
                "'mercadolibre.gradle.config.library'"
            }

        val gradlePath =
            if (productive && type == ModuleType.APP) {
                "templates/moduleAppBuildGradle.gradle"
            } else {
                "templates/moduleBuildGradle.gradle"
            }

        fileManager.createFile(
            "$name/build.gradle",
            "apply plugin: $plugin\n" + fileManager.readFile(gradlePath)
                .replace("tmpFolder", "${tmpFolder.root}")
        )
    }

    private fun configModule(project: Project) {
        try {
            val gss = (project as DefaultProject).services as ProjectScopeServices
            val state: Field = ProjectScopeServices::class.java.superclass.getDeclaredField("state")
            state.isAccessible = true
            val stateValue: AtomicReference<Any> = state.get(gss) as AtomicReference<Any>
            val enumClass = Class.forName(DefaultServiceRegistry::class.java.name + "\$State")
            stateValue.set(enumClass.enumConstants[0])
            gss.add(BuildEventsListenerRegistry::class.java, FakeBuildEventsListenerRegistry())
            stateValue.set(enumClass.enumConstants[1])
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    internal class FakeBuildEventsListenerRegistry : BuildEventsListenerRegistry {
        override fun onTaskCompletion(provider: Provider<out OperationCompletionListener?>?) {}
    }
}
