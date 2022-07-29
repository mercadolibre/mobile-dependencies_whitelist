package com.mercadolibre.android.gradle.baseplugin.managers

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer

data class MockedProjectContent(
    val project: Project,
    val extension: ExtensionContainer,
    val extensions: MutableMap<String, Any>,
    val variants: ArrayList<BaseVariant>,
    val tasks: TaskContainer,
    val configurations: ArrayList<Configuration>
)

data class MockedRootProject(
    val projectContent: MockedProjectContent,
    val subProjects: MutableMap<String, MockedProjectContent>
)
