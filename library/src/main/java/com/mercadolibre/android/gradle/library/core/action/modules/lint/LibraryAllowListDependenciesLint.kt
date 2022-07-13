package com.mercadolibre.android.gradle.library.core.action.modules.lint

import com.android.build.gradle.api.BaseVariant
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOWLIST_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.API_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.COMPILE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.EXPIRES_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.IMPLEMENTATION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_DEPENDENCIES_TASK
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_ALLOWED_DEPENDENCIES_SUFFIX
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_ERROR_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_DESCRIPTION
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNIGN_TITLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNING_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies.Status
import com.mercadolibre.android.gradle.library.core.action.modules.lint.dependencies.StatusBase
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.net.URL
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class LibraryAllowListDependenciesLint : Lint() {

    private val FILE_BLOCKER = "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_FILENAME"
    private val FILE_WARNING = "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_WARNING_FILENAME"

    private val DEFAULT_GRADLE_VERSION_VALUE = "unspecified"

    var hasFailed = false

    val ALLOWLIST_DEPENDENCIES = arrayListOf<Dependency>()
    val ALLOWLIST_GOING_TO_EXPIRE = arrayListOf<Dependency>()

    override fun name(): String {
        return LINT_DEPENDENCIES_TASK
    }

    override fun lint(project: Project, variants: List<BaseVariant>): Boolean {
        hasFailed = false
        findExtension<LintGradleExtension>(project)?.apply {
            if (!dependenciesLintEnabled) {
                return false
            }
            if (project.plugins.hasPlugin("com.android.library")) {
                setUpAllowlist(dependencyAllowListUrl)

                for (variant in variants) {
                    val variantName = variant.name
                    analyzeDependency(project, "${variantName}${IMPLEMENTATION_CONSTANT.capitalized()}")
                    analyzeDependency(project, "${variantName}${API_CONSTANT.capitalized()}")
                    analyzeDependency(project, "${variantName}${COMPILE_CONSTANT.capitalized()}")
                }

                analyzeDependency(project, API_CONSTANT)
                analyzeDependency(project, IMPLEMENTATION_CONSTANT)
                analyzeDependency(project, COMPILE_CONSTANT)

                if (hasFailed) {
                    report(
                        "$LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX ${dependencyAllowListUrl}\n$LINT_ERROR_ALLOWED_DEPENDENCIES_SUFFIX",
                        project
                    )
                }

                if (ALLOWLIST_GOING_TO_EXPIRE.size > 0) {
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

    fun reportWarnings(project: Project) {
        val file = project.file(FILE_WARNING)
        if (project.file(FILE_WARNING).exists()) {
            project.file(FILE_WARNING).delete()
        } else {
            file.parentFile.mkdirs()
        }

        var message = "$LINT_WARNIGN_TITLE \n"
        for (dependency in ALLOWLIST_GOING_TO_EXPIRE) {
            message += "(${findDependencyInList(dependency, ALLOWLIST_DEPENDENCIES)?.rawExpiresDate})" +
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
        val file = project.file(FILE_BLOCKER)
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

    fun analyzeDependency(dependency: Dependency, project: Project) {
        val dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"
        val isLocalModule = project.rootProject.allprojects.find {
            dependencyFullName.contains("${it.group}:${it.name}")
        } != null

        if (!dependencyFullName.contains(DEFAULT_GRADLE_VERSION_VALUE) && !isLocalModule) {
            val result = getStatusDependencyInAllowList(dependency)
            if (result.isBlocker) {
                report(result.message(dependencyFullName), project)
            } else if (result.shouldReport) {
                ALLOWLIST_GOING_TO_EXPIRE.add(dependency)
            }
        }
    }

    private fun getStatusDependencyInAllowList(dependency: Dependency): StatusBase {
        val dep = findDependencyInList(dependency, ALLOWLIST_DEPENDENCIES)
        if (dep != null) {
            return if (dep.expires == null) {
                Status.available()
            } else {
                when {
                    dep.expires == Long.MAX_VALUE -> {
                        Status.available()
                    }
                    System.currentTimeMillis() < dep.expires -> {
                        Status.goign_to_expire()
                    }
                    else -> {
                        Status.expired()
                    }
                }
            }
        }
        return Status.invalid()
    }

    fun getVariableFromJson(name: String, json: JsonElement, defaultValue: String?): String? {
        return if (json.asJsonObject[name] != null) {
            json.asJsonObject[name].asString.replace("\\", "")
        } else {
            defaultValue
        }
    }

    fun castStringToDate(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd").parse(date.replace("\\", "")).time
    }

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
                ALLOWLIST_DEPENDENCIES.add(jsonNodeToDependency(it))
            }
        }
    }

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
