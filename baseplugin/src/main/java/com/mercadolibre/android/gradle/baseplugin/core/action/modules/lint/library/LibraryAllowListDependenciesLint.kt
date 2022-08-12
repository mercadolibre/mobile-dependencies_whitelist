package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOWLIST_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_BLOCKER
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_WARNING
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import org.gradle.api.Project
import java.net.URL
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * The LibraryAllowListDependenciesLint class is in charge of reviewing all the dependencies of the project through the AllowList to
 * report if there is any deprecated in a Library.
 */
class LibraryAllowListDependenciesLint(private val variantNames: List<String>) : Lint() {

    private val defaultGradleVersion = "unspecified"

    /** This variable contains the output of the lint report. */
    var hasFailed = false

    /** This list contains the dependencies in the allow list. */
    val allowListDependencies = arrayListOf<Dependency>()
    /** This list contains the dependencies that are about to expire. */
    val allowListGoingToExpire = arrayListOf<Dependency>()

    /**
     * This method is responsible for providing a name to the linteo class.
     */
    override fun name(): String = LINT_DEPENDENCIES_TASK

    /**
     * This method is responsible for verifying that the dependencies of all the variants are valid or
     * if they are about to expire, perform the warning.
     */
    override fun lint(project: Project): Boolean {
        hasFailed = false
        findExtension<LintGradleExtension>(project)?.apply {
            if (!dependenciesLintEnabled) {
                return false
            }
            if (project.plugins.hasPlugin("com.android.library")) {
                setUpAllowlist(dependencyAllowListUrl)

                for (variantName in variantNames) {
                    analyzeDependency(project, variantName)
                }

                if (hasFailed) {
                    report(
                        LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX.replace("URL", dependencyAllowListUrl),
                        project
                    )
                }

                if (allowListGoingToExpire.size > 0) {
                    reportWarnings(project)
                }
            }
        }
        return hasFailed
    }

    private fun analyzeDependency(project: Project, variantName: String) {
        project.configurations.findByName(variantName)?.apply {
            for (dependency in dependencies) {
                analyzeDependency(Dependency(dependency.group, dependency.name, dependency.version, 0, ""), project)
            }
        }
    }

    /**
     * This method is responsible for generating reports in case there are dependencies that have warnings.
     */
    fun reportWarnings(project: Project) {
        val file = project.file(LINT_LIBRARY_FILE_WARNING)
        if (project.file(LINT_LIBRARY_FILE_WARNING).exists()) {
            project.file(LINT_LIBRARY_FILE_WARNING).delete()
        } else {
            file.parentFile.mkdirs()
        }

        var message = "$LINT_WARNIGN_TITLE \n"
        for (dependency in allowListGoingToExpire) {
            message += "(${findDependencyInList(dependency, allowListDependencies)?.rawExpiresDate})" +
                " - ${dependency.group}:${dependency.name}:${dependency.version} (Deprecated!)\n"
        }
        message += "\n$LINT_WARNIGN_DESCRIPTION\n"
        println(message)
        file.appendText(message)
    }

    private fun findDependencyInList(dependency: Dependency, list: ArrayList<Dependency>): Dependency? {
        val dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"
        for (allowListDep in list) {
            val pattern = Pattern.compile(
                "${allowListDep.group}:${allowListDep.name}:(${allowListDep.version})",
                Pattern.CASE_INSENSITIVE
            )
            if (pattern.matcher(dependencyFullName).matches()) {
                return allowListDep
            }
        }
        return null
    }

    private fun report(message: String, project: Project) {
        val file = project.file(LINT_LIBRARY_FILE_BLOCKER)
        if (!hasFailed) {
            if (!file.exists()) {
                file.parentFile.mkdirs()
            }
            hasFailed = true
            println("\n" + LINT_ERROR_TITLE)
            file.writeText(LINT_ERROR_TITLE)
            file.appendText("${System.getProperty("line.separator")}$message")
        }

        println(message)
    }

    /**
     * This method is responsible for verifying if the dependency has to be reported, or has any warning.
     */
    fun analyzeDependency(dependency: Dependency, project: Project) {
        val dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"
        val isLocalModule = project.rootProject.allprojects.find {
            dependencyFullName.contains("${it.group}:${it.name}")
        } != null

        if (!dependencyFullName.contains(defaultGradleVersion) && !isLocalModule) {
            val result = getStatusDependencyInAllowList(dependency)
            if (result.isBlocker) {
                report(result.message(dependencyFullName), project)
            } else if (result.shouldReport) {
                allowListGoingToExpire.add(dependency)
            }
        }
    }

    private fun getStatusDependencyInAllowList(dependency: Dependency): StatusBase {
        val dep = findDependencyInList(dependency, allowListDependencies)
        if (dep != null) {
            return if (dep.expires == null) {
                Status.available()
            } else {
                when {
                    dep.expires == Long.MAX_VALUE -> {
                        Status.available()
                    }
                    System.currentTimeMillis() < dep.expires -> {
                        Status.goignToExpire()
                    }
                    else -> {
                        Status.expired()
                    }
                }
            }
        }
        return Status.invalid()
    }

    /**
     * This method is responsible for obtaining data from a Json safely.
     */
    fun getVariableFromJson(name: String, json: JsonElement, defaultValue: String?): String? = if (json.asJsonObject[name] != null) {
        json.asJsonObject[name].asString.replace("\\", "")
    } else {
        defaultValue
    }

    private fun castStringToDate(date: String): Long = SimpleDateFormat("yyyy-MM-dd").parse(date.replace("\\", "")).time

    /**
     * This method is in charge of casting a Json element to a Date in a safe way.
     */
    fun castJsonElementToDate(it: JsonElement): Long? {
        val element = it.asJsonObject[EXPIRES_CONSTANT]
        if (element == null || element.asString == "null") {
            return null
        }
        return castStringToDate(element.asString)
    }

    private fun setUpAllowlist(allowListUrl: String) {
        with(URL(allowListUrl).openConnection()) {
            val json = JsonParser.parseReader(getInputStream().reader())

            json.asJsonObject[ALLOWLIST_CONSTANT].asJsonArray.all {
                allowListDependencies.add(jsonNodeToDependency(it))
            }
        }
    }

    /**
     * This method is responsible for obtaining the nodes of the dependencies and storing them through the Data Class Dependency.
     */
    fun jsonNodeToDependency(it: JsonElement): Dependency {
        val expires: Long? = castJsonElementToDate(it.asJsonObject)

        return Dependency(
            it.asJsonObject[GROUP_CONSTANT].asString.replace("\\", ""),
            getVariableFromJson(NAME_CONSTANT, it, ".*"),
            getVariableFromJson(VERSION_CONSTANT, it, ".*"),
            expires,
            getVariableFromJson(EXPIRES_CONSTANT, it, null),
        )
    }
}
