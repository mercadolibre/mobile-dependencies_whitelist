package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

import com.android.build.gradle.api.BaseVariant
import com.google.gson.JsonParser
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Lint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.LintGradleExtension
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.Status
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics.StatusBase
import com.mercadolibre.android.gradle.baseplugin.core.components.ALLOWLIST_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.ANDROID_LIBRARY_PLUGIN
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
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_WARNING_FILENAME
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.RAW_EXPIRES_DATE_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import java.net.URL
import java.text.SimpleDateFormat
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class LibraryWhitelistedDependenciesLint: Lint() {

    private val FILE_BLOCKER = "build/reports/${LibraryWhitelistedDependenciesLint::class.java.simpleName}/${LINT_FILENAME}"
    private val FILE_WARNING = "build/reports/${LibraryWhitelistedDependenciesLint::class.java.simpleName}/${LINT_WARNING_FILENAME}"

    private val DEFAULT_GRADLE_VERSION_VALUE = "unspecified"

    private var hasFailed = false

    val WHITELIST_DEPENDENCIES = arrayListOf<Dependency>()
    val WHITELIST_GOING_TO_EXPIRE = arrayListOf<Dependency>()

    override fun name(): String {
        return LINT_DEPENDENCIES_TASK
    }

    override fun lint(project: Project, variants: ArrayList<BaseVariant>): Boolean {
        findExtension<LintGradleExtension>(project)?.apply {
            if (!dependenciesLintEnabled){
                return false
            }
            if (project.plugins.hasPlugin(ANDROID_LIBRARY_PLUGIN)) {
                setUpAllowlist(dependencyWhitelistUrl)

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
                        "$LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX ${dependencyWhitelistUrl}\n${LINT_ERROR_ALLOWED_DEPENDENCIES_SUFFIX}",
                        project
                    )
                }

                if (WHITELIST_GOING_TO_EXPIRE.size > 0) {
                    reportWarnings(project)
                }
            }
        }
        return hasFailed
    }

    private fun analyzeDependency(project: Project, variantName: String) {
        project.configurations.findByName(variantName)?.apply {
            for (dependency in allDependencies) {
                analizeDependency(Dependency(dependency.group!!, dependency.name, dependency.version!!, 0, ""), project)
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

        var message = "WARNING: The following dependencies has been marked as deprecated: \n"
        for (dependency in WHITELIST_GOING_TO_EXPIRE) {
            message += "(${findDependencyInList(dependency, WHITELIST_DEPENDENCIES)?.rawExpiresDate}) - $dependency (Deprecated!)\n"
        }
        message += "\nYou should consider upgrading the lib OR contact the team owner to know how to proceed.\n"
        println(message)
        file.appendText(message)
    }

    private fun findDependencyInList(dependency: Dependency, list: ArrayList<Dependency>): Dependency? {
        for (whitelistDep in list) {
            if (dependency.name != null) {
                println(whitelistDep)
                if (dependency.group.contains(whitelistDep.group)) {
                    return if (whitelistDep.name != null) {
                        if (dependency.name.contains(whitelistDep.name)){
                            whitelistDep
                        } else {
                            null
                        }
                    } else {
                        whitelistDep
                    }
                }
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
        }

        file.appendText("${System.getProperty("line.separator")}${message}")
        println(message)
    }

    fun analizeDependency(dependency: Dependency, project: Project) {
        val dependencyFullName = "${dependency.group}:${dependency.name}:${dependency.version}"
        val isLocalModule = project.rootProject.allprojects.find {
            dependencyFullName.contains("${it.group}:${it.name}")
        } != null

        if (!dependencyFullName.contains(DEFAULT_GRADLE_VERSION_VALUE) && !isLocalModule) {
            val result = getStatusDependencyInWhitelist(dependency)
            if (result.isBlocker) {
                report(result.message(dependencyFullName, name()), project)
            } else if (result.shouldReport) {
                WHITELIST_GOING_TO_EXPIRE.add(dependency)
            }
        }
    }

    private fun getStatusDependencyInWhitelist(dependency: Dependency): StatusBase {
        val dep = findDependencyInList(dependency, WHITELIST_DEPENDENCIES)
        if (dep != null) {
            return if (dep.expires == null) {
                Status().available()
            } else {
                when {
                    dep.expires == Long.MAX_VALUE -> { Status().available() }
                    System.currentTimeMillis() < dep.expires -> { Status().goign_to_expire() }
                    else -> { Status().expired() }
                }
            }

        }
        return Status().invalid()
    }

    private fun setUpAllowlist(whitelistUrl: String) {
        with(URL(whitelistUrl).openConnection()){
            val json = JsonParser.parseReader(getInputStream().reader())

            json.asJsonObject[ALLOWLIST_CONSTANT].asJsonArray.all {
                val version: String? =
                    if (it.asJsonObject[VERSION_CONSTANT] != null) {
                        it.asJsonObject[VERSION_CONSTANT].asString
                    } else {
                        null
                    }

                val expiresRaw: String? =
                    if (it.asJsonObject[RAW_EXPIRES_DATE_CONSTANT] != null) {
                        it.asJsonObject[RAW_EXPIRES_DATE_CONSTANT].asString
                    } else {
                        null
                    }

                val expires: Long? =
                    if (it.asJsonObject[EXPIRES_CONSTANT] != null) {
                        SimpleDateFormat("yyyy-MM-dd").parse(it.asJsonObject[EXPIRES_CONSTANT].asString).time
                    } else {
                        null
                    }

                val name: String? =
                    if (it.asJsonObject[NAME_CONSTANT] != null) {
                        it.asJsonObject[NAME_CONSTANT].asString
                    } else {
                        null
                    }

                WHITELIST_DEPENDENCIES.add(Dependency(
                    it.asJsonObject[GROUP_CONSTANT].asString,
                    name,
                    version,
                    expires,
                    expiresRaw,
                ))
            }
        }
    }

}