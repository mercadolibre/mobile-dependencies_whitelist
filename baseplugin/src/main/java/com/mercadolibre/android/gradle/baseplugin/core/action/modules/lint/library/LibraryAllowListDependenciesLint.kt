package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.library

import com.google.gson.JsonElement
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.AlphaAllowedProjects
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.DependencyDataInAllowList
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.JsonUtils
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.OutputUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOWLIST_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.ARROW
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.IS_ALPHA
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_POSTDATA
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_BLOCKER
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_LIBRARY_FILE_WARNING
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import org.gradle.api.Project
import java.util.regex.Pattern

/**
 * The LibraryAllowListDependenciesLint class is in charge of reviewing all the dependencies of the project through the AllowList to
 * report if there is any deprecated in a Library.
 */
class LibraryAllowListDependenciesLint(private val variantNames: List<String>) : Lint() {

    private val URL_PLACEHOLDER = "<URL>"

    private val UNDEFINED_VERSION = ".*"

    private val defaultGradleVersion = "unspecified"

    private var hasFailed = false

    /** This list contains the dependencies in the allow list. */
    val allowListDependencies = arrayListOf<Dependency>()

    /** This list contains the dependencies that are about to expire. */
    private val allowListGoingToExpire = arrayListOf<Dependency>()

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
            if (dependenciesLintEnabled) {
                getAllowList(dependencyAllowListUrl)
                analyzeDependencies(project)
                if (hasFailed) {
                    report(
                        LINT_ERROR_POSTDATA.replace(URL_PLACEHOLDER, dependencyAllowListUrl),
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

    private fun getAllowList(url: String) {
        JsonUtils.getJson(url)[ALLOWLIST_CONSTANT].asJsonArray.all {
            allowListDependencies.add(jsonNodeToDependency(it))
        }
    }

    private fun analyzeDependencies(project: Project) {
        for (variantName in variantNames) {
            analyzeDependency(project, variantName)
        }
    }

    private fun analyzeDependency(project: Project, variantName: String) {
        project.configurations.findByName(variantName)?.apply {
            for (dependency in dependencies) {
                analyzeDependency(
                    with(dependency) {
                        Dependency(group, name, version, 0, "", false)
                    },
                    project
                )
            }
        }
    }

    /**
     * This method is responsible for generating reports in case there are dependencies that have warnings.
     */
    private fun reportWarnings(project: Project) {
        val file = project.file(LINT_LIBRARY_FILE_WARNING).apply {
            // Warning file Exist ?
            if (exists()) {
                // Delete old warnings
                delete()
            } else {
                // Make the dir to save new Warnings
                parentFile.mkdirs()
            }
        }

        with(OutputUtils) {
            writeAReportMessage(LINT_WARNIGN_TITLE, file)
            logWarning(LINT_WARNIGN_TITLE)

            for (dependency in allowListGoingToExpire) {
                val depAllowListData = findDependencyInList(
                    project,
                    dependency,
                    allowListDependencies
                )

                val expireDate = "(${depAllowListData.allowListDep?.rawExpiresDate})"
                val dependencyData = " - ${dependency.group}:${dependency.name}:${dependency.version} (Deprecated!) "
                val availableVersion = if (depAllowListData.availableVersion != null) {
                    "Available version $ARROW ${depAllowListData.availableVersion}"
                } else {
                    ""
                }

                logMessage(expireDate + dependencyData + availableVersion)
                writeAReportMessage(expireDate + dependencyData + availableVersion, file)
            }

            logMessage(LINT_WARNIGN_DESCRIPTION)
            writeAReportMessage(LINT_WARNIGN_DESCRIPTION, file)
        }
    }

    private fun findDependencyInList(
        project: Project,
        dependency: Dependency,
        allowList: ArrayList<Dependency>
    ): DependencyDataInAllowList {
        val depAllowListData = DependencyDataInAllowList(null, null)

        val dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"

        // Iterate on dependencies in allowlist
        for (allowListDep in allowList) {
            val dependencyPattern =
                "${allowListDep.group}:${allowListDep.name}:(${allowListDep.version})"

            // Check if this dependency is the same as the one currently iterating
            if (Pattern.compile(dependencyPattern, Pattern.CASE_INSENSITIVE)
                    .matcher(dependencyFullName).matches()
            ) {
                depAllowListData.allowListDep = allowListDep
            }

            // Check if this dependency is one of the available versions of the dependency being iterated
            if (isTheSameDependency(dependency, allowListDep) && isAllowedByDeadline(allowListDep)) {
                depAllowListData.availableVersion = allowListDep.version?.replace("|", " or ")
            }

            findExtension<LintGradleExtension>(project)?.apply {
                if (alphaDependenciesEnabled
                    && !isAllowedByAlpha(project, allowListDep)
                ) {
                    depAllowListData.availableVersion = null
                }
            }
        }
        return depAllowListData
    }

    private fun isAllowedByDeadline(allowListDep: Dependency): Boolean {
        return allowListDep.expires == null || allowListDep.expires == Long.MAX_VALUE
    }

    private fun isAllowedByAlpha(project: Project, allowListDep: Dependency): Boolean {
        var alphaAllowed = false
        allowListDep.isAlpha?.let { allowListAlphaEnabled ->
            if (allowListAlphaEnabled)
                AlphaAllowedProjects.groups.forEach { alphaAllowedGroup ->
                    if (alphaAllowedGroup == project.group) {
                        alphaAllowed = true
                    }
                }
        }
        return alphaAllowed
    }

    private fun isTheSameDependency(dependency: Dependency, allowListDep: Dependency): Boolean {
        return allowListDep.group == dependency.group &&
                (allowListDep.name == UNDEFINED_VERSION || (allowListDep.name == dependency.name || allowListDep.name == null))
    }

    private fun report(message: String, project: Project) {
        val file = project.file(LINT_LIBRARY_FILE_BLOCKER)
        with(OutputUtils) {
            if (!hasFailed) {
                hasFailed = true
                writeAReportMessage(LINT_ERROR_TITLE, file)
                logError(LINT_ERROR_TITLE)
            }
            logMessage(message)
            writeAReportMessage("\n$message", file)
        }
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
            val result = getStatusDependencyInAllowList(project, dependency)
            if (result.isBlocker) {
                report(result.message(dependencyFullName), project)
            } else if (result.shouldReport) {
                allowListGoingToExpire.add(dependency)
            }
        }
    }

    private fun getStatusDependencyInAllowList(
        project: Project,
        dependency: Dependency
    ): StatusBase {
        val depAllowListData = findDependencyInList(
            project,
            dependency,
            allowListDependencies
        )
        depAllowListData.allowListDep?.let { data ->
            return if (data.expires == null) {
                Status.available()
            } else if (dependency.isAlpha == true
                && depAllowListData.availableVersion == null
            ) {
                Status.alphaDenied()
            } else {
                when {
                    data.expires == Long.MAX_VALUE -> {
                        Status.available()
                    }

                    System.currentTimeMillis() < data.expires -> {
                        Status.goingToExpire(depAllowListData.availableVersion)
                    }

                    else -> {
                        Status.expired(depAllowListData.availableVersion)
                    }
                }
            }
        }
        return Status.invalid(depAllowListData.availableVersion)
    }

    /**
     * This method is responsible for obtaining the nodes of the dependencies and storing them through the Data Class Dependency.
     */
    fun jsonNodeToDependency(it: JsonElement): Dependency {
        val expires: Long? = JsonUtils.castJsonElementToDate(it.asJsonObject)

        return Dependency(
            it.asJsonObject[GROUP_CONSTANT].asString.replace("\\", ""),
            JsonUtils.getStringVariableFromJson(NAME_CONSTANT, it, UNDEFINED_VERSION),
            JsonUtils.getStringVariableFromJson(VERSION_CONSTANT, it, UNDEFINED_VERSION),
            expires,
            JsonUtils.getStringVariableFromJson(EXPIRES_CONSTANT, it, null),
            JsonUtils.getBooleanVariableFromJson(IS_ALPHA, it, false)
        )
    }
}
