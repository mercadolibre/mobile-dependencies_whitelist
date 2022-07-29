package com.mercadolibre.android.gradle.dynamicfeature.managers

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

    fun createSampleSubProject(name: String, tmpFolder: TemporaryFolder, root: Project): Project {
        val tmpProjectFolder = tmpFolder.newFolder(name)
        val project = ProjectBuilder.builder().withProjectDir(tmpProjectFolder).withName(name).withParent(root).build()
        configModule(project)
        return project
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
