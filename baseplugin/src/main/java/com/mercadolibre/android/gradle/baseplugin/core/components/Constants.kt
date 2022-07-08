package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.LibraryAllowListDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.ReleaseDependenciesLint
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.AndroidProjectTypes
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider

/*****************************************
 *                BASE                    *
 *******************************************/

const val ANDROID_USER_NAME = "AndroidInternalReleasesUsername"
const val ANDROID_USER_PASSWORD = "AndroidInternalReleasesPassword"
const val MELI_GROUP = "meliPlugin"
const val RESOLUTION_STRATEGY_HOURS = 2

const val GRADLE_ENTERPRISE = "com.gradle.enterprise"
const val MAVEN_PUBLISH = "org.gradle.maven-publish"

val LIBRARY_PLUGINS = listOf(
    "kotlin-android",
    "com.android.library"
)

val APP_PLUGINS = listOf(
    "kotlin-android",
    "com.android.application"
)

const val ANSI_RESET = "\u001B[0m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_BOLD = "\u001B[0;1m"

val SEPARATOR = "------------------------------------------------------------------------".ansi(ANSI_BLACK)
const val ARROW = "-->"

infix fun String.ansi(ansiColor: String): String {
    return ansiColor + this + ANSI_RESET
}

infix fun Int.ansi(ansiColor: String): String {
    return ansiColor + this.toString() + ANSI_RESET
}

/*****************************************
 *                CONFIGURERS             *
 *******************************************/

val ANDROID_CONFIGURER_DESCRIPTION = """
${AndroidConfigurer::class.java.simpleName.ansi(ANSI_BOLD)}
Responsible for sending the minimum configurations for an Android module, sending the variables:
- ${"Compile SDK".ansi(ANSI_YELLOW)}            $ARROW ${VersionProvider.provideApiSdkLevel().ansi(ANSI_GREEN)}
- ${"Build Tools Version".ansi(ANSI_YELLOW)}    $ARROW ${VersionProvider.provideBuildToolsVersion().ansi(ANSI_GREEN)}
- ${"Min Sdk Version".ansi(ANSI_YELLOW)}        $ARROW ${VersionProvider.provideMinSdk().ansi(ANSI_GREEN)}
- ${"Target Sdk Version".ansi(ANSI_YELLOW)}     $ARROW ${VersionProvider.provideApiSdkLevel().ansi(ANSI_GREEN)}
- ${"Source Compatibility".ansi(ANSI_YELLOW)}   $ARROW ${VersionProvider.provideJavaVersion().toString().ansi(ANSI_GREEN)}
- ${"Target Compatibility".ansi(ANSI_YELLOW)}   $ARROW ${VersionProvider.provideJavaVersion().toString().ansi(ANSI_GREEN)}
""".trimIndent()

val BASICS_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${BasicsConfigurer::class.java.simpleName.ansi(ANSI_BOLD)} 
Responsible for configuring the meli proxy as well as managing cached versions.
- ${"Resolution Strategy by Hours".ansi(ANSI_YELLOW)} $ARROW ${RESOLUTION_STRATEGY_HOURS.ansi(ANSI_GREEN)}
- ${"Repositories".ansi(ANSI_YELLOW)}${"\n"}${RepositoryProvider().getRepositoriesDescription()}
""".trimIndent()

val EXTENSIONS_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${ExtensionsConfigurer::class.java.simpleName.ansi(ANSI_BOLD)}
Responsible for generating the necessary extensions to configure the modules.${"\n"}${ExtensionsConfigurer().getExtensions()}
""".trimIndent()

val MODULE_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${ModuleConfigurer::class.java.simpleName}
Responsible for requesting the modules to configure their tasks within the projects.
- ${BasePlugin::class.java.simpleName.ansi(ANSI_YELLOW)}
${ModuleConfigurer().getModules("Java Modules", ModuleProvider.provideJavaModules())}
${ModuleConfigurer().getModules("Project Modules", ModuleProvider.provideProjectModules())}
${ModuleConfigurer().getModules("Settings Modules", ModuleProvider.provideSettingsModules())}
            
""".trimIndent()

val PLUGIN_CONFIGURER_DESCRITION = """
$SEPARATOR
${PluginConfigurer::class.java.simpleName.ansi(ANSI_BOLD)}
Responsible for providing the functionality to implement plugins in the project, also the basic plugins.
- ${PluginConfigurer::class.java.simpleName.ansi(ANSI_YELLOW)}        $ARROW ${ANSI_GREEN}apply function$ANSI_RESET
- ${"LibraryPluginConfigurer".ansi(ANSI_YELLOW)} $ARROW ${LIBRARY_PLUGINS.toString().replace("[", "").replace("]", "").ansi(ANSI_GREEN)}
- ${"AppPluginConfigurer".ansi(ANSI_YELLOW)}     $ARROW ${APP_PLUGINS.toString().replace("[", "").replace("]", "").ansi(ANSI_GREEN)}
""".trimIndent()

val ALL_PLUGIN_DESCRIPTION = """            
    
    
    
Meli Plugin of Gradle - Configs
$SEPARATOR
                
$ANDROID_CONFIGURER_DESCRIPTION
$BASICS_CONFIGURER_DESCRIPTION
$EXTENSIONS_CONFIGURER_DESCRIPTION
$PLUGIN_CONFIGURER_DESCRITION
""".trimIndent()

/*****************************************
 *                MODULES                 *
 *******************************************/

const val JACOCO_PLUGIN = "org.gradle.jacoco"
const val JACOCO_EXTENSION = "jacocoConfiguration"
const val JACOCO_GROUP = "reporting"
const val JACOCO_VERIFICATION_GROUP = "verification"
const val JACOCO_FULL_REPORT_TASK = "jacocoFullReport"
const val JACOCO_TEST_REPORT_TASK = "jacocoTestReport"
const val JACOCO_TEST_REPORT_DESCRIPTION = "Generates Jacoco coverage reports"
const val JACOCO_REPORT_FLAVOR_TEST_TASK_NAME = "jacocoTestvariantUnitTestReport"
const val JACOCO_REPORT_TASK_DESCRIPTION = "Generates Jacoco coverage reports for the build variant."
val JACOCO_ANDROID_EXCLUDE = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.class",
    "**/*\$ViewInjector*.*",
    "**/*\$ViewBinder*.*",
    "**/Manifest*.*",
    "**/*\$Lambda$*.*",
    "**/*Module.*",
    "**/*Dagger*.*",
    "**/*MembersInjector*.*",
    "**/*_Provide*Factory*.*",
    "**/*_Factory*.*",
    "**/*$*$*.*"
)

const val TEST_TASK = "test"
const val UNIT_TEST_GROUP = "verification"
const val UNIT_TEST_FLAVOR_TEST_TASK_NAME = "testvariantUnitTest"
const val UNIT_TEST_TASK_DESCRIPTION = "Run unit tests for the build."

const val LINTABLE_TASK = "lintGradle"
const val LINTABLE_EXTENSION = "lintGradle"
const val LINTABLE_DESCRIPTION = "Lints the project dependencies to check they are in the allowed allowlist"
const val LINT_DEPENDENCIES_TASK = "lintDependencies"
const val LINT_RELEASE_DEPENDENCIES_TASK = "lintReleaseDependencies"
const val LINT_ERROR_TITLE = "ERROR: The following dependencies are not allowed:"
const val LINT_WARNIGN_TITLE = "WARNING: The following dependencies has been marked as deprecated:"
const val LINT_TASK_FAIL_MESSAGE = "Errors found while running lints, please check the console output for more information"
const val LINT_WARNIGN_DESCRIPTION = "You should consider upgrading the lib OR contact the team owner to know how to proceed."
const val LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX = "\nYour project can only contain the dependencies listed in: URL \n" +
    "If you think one of them should be in the allowlist, please start here " +
    "https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas\n"
const val LINT_FILENAME = "lint.ld"
const val LINT_WARNING_FILENAME = "lintWarning.ld"
const val LINT_AVAILABLE = "available"
const val LINT_INVALID = "invalid"
const val LINT_EXPIRED = "expired"
const val LINT_GOING_TO_EXPIRE = "going to expire"
const val LINT_REPORT_ERROR = "Cant report this type of dependency"
const val LINT_RELEASE_ERROR_TITLE = "Error. Found non-release dependencies in the module release version:"
val LINT_LIBRARY_FILE_BLOCKER = "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_FILENAME"
val LINT_LIBRARY_FILE_WARNING = "build/reports/${LibraryAllowListDependenciesLint::class.java.simpleName}/$LINT_WARNING_FILENAME"
val LINT_RELEASE_FILE = "build/reports/${ReleaseDependenciesLint::class.java.simpleName}/$LINT_FILENAME"

const val LIST_VARIANTS_TASK = "listVariants"
const val LIST_VARIANTS_DESCRIPTION = "List all variant in this project"

const val LIST_PROJECTS_TASK = "listProjects"
const val LIST_PROJECTS_DESCRIPTION = "List all subprojects in this project"
const val BEGINNING_TOKEN = "=== BEGINNING OF PROJECTS LIST ==="
val TYPE_NOT_RECOGNISED_MESSAGE = "Specified project type not recognised. Project types available are " + AndroidProjectTypes.values()

const val PLUGIN_DESCRIPTION_TASK = "pluginDescription"
const val APP_PLUGIN_DESCRIPTION_TASK = "appPluginDescription"
const val LIBRARY_PLUGIN_DESCRIPTION_TASK = "libraryPluginDescription"
const val PLUGIN_MODULES_DESCRIPTION_TASK = "pluginsModulesDescription"
const val PLUGIN_DESCRIPTION_DESCRIPTION = "Describes the settings managed by the plugin"

const val TASK_GET_PROJECT_TASK = "getProjectVersion"
const val TASK_GET_PROJECT_DESCRIPTION = "Gets project version"
const val FILE_NAME_PROJECT_VERSION = "project.version"

const val UNPACK_DEBUG_KEY_STORE_GROUP = "keystore"
const val UNPACK_DEBUG_KEY_STORE_TASK = "unpackDebugKeystore"
const val UNPACK_DEBUG_KEY_STORE_DESCRIPTION = "Unpack the debug keystore into the build directory of the project"
const val DIRECTORY_NAME = "keystores"
const val FILE_NAME_DEBUG_KEY = "debug_keystore"
const val KEY_STORE_PASSWORD = "android"
const val KEY_ALIAS = "androiddebugkey"
const val KEY_PASSWORD = "android"

const val PUBLISHING_GROUP = "publishing"
const val DOCUMENTATION_GROUP = "Documentation"
const val PACKAGING_GROUP = "packaging"
const val PUBLISHING_TIME_GENERATOR = "yyyyMMddHHmmss"
const val PUBLISHING_PRINT_MESSAGE = "Publishing version: "
const val PUBLISHING_TIME_ZONE = "UTC"
const val PUBLISHING_JAVADOC_TASK = "Javadoc"
const val PUBLISHING_JAVADOC_DESCRIPTION = "Generates Javadoc for"
const val PUBLISHING_SOURCES_TASK = "Sources"
const val PUBLISHING_EXPERIMENTAL = "EXPERIMENTAL"
const val PUBLISHING_LOCAL = "LOCAL"
const val PUBLISHING_EXPERIMENTAL_SUBFIX_TASK = "$PUBLISHING_EXPERIMENTAL-"
const val PUBLISHING_LOCAL_SUBFIX_TASK = "$PUBLISHING_LOCAL-"
const val PUBLISHING_MAVEN_LOCAL = "MavenLocal"
const val SOURCE_SETS_TEST = "test"
const val SOURCE_SETS_DEFAULT = "main"
const val JAVA_COMPILE_PROVIDER = "javaCompileProvider"
const val PACKAGE_LIBRARY_PROVIDER = "packageLibraryProvider"
const val PUBLISHING_POM_FILE = "pom-default.xml"
const val POM_FILE_TASK = "generatePomFileFor"
const val TASK_TYPE_RELEASE = "Release"
const val TASK_TYPE_EXPERIMENTAL = "Experimental"
const val TASK_TYPE_LOCAL = "Local"
const val TASK_TYPE_PUBLIC_RELEASE = "PublicRelease"
const val TASK_TYPE_PRIVATE_RELEASE = "PrivateRelease"
val PUBLISHING_EXCLUDES_ARR = mutableListOf(
    "**/BuildConfig.java",
    "**/R.java"
)
val PUBLISHING_LINKS_ARR = mutableListOf(
    "http://docs.oracle.com/javase/7/docs/api/",
    "http://d.android.com/reference/"
)
val PUBLISHING_LINKS_JAR = mutableListOf(
    "http://docs.oracle.com/javase/7/docs/api/"
)
val PUBLISHING_OPTIONS = mutableMapOf(
    "Xdoclint:none" to "-quiet"
)

/*****************************************
 *                MISC                    *
 *******************************************/

const val RELEASE_CONSTANT = "release"
const val MDS_CONSTANT = "mds"
const val DEBUG_CONSTANT = "debug"
const val FLAVOR_CONSTANT = "flavor"
const val VARIANT_CONSTANT = "variant"
const val BUILD_CONSTANT = "build"
const val PACKAGING_AAR_CONSTANT = "Aar"
const val PACKAGING_JAR_CONSTANT = "Jar"
const val DIR_CONSTANT = "dir"
const val EXCLUDES_CONSTANT = "excludes"
const val TYPE_CONSTANT = "type"
const val DEFAULT_CONSTANT = "default"
const val ARCHIVES_CONSTANT = "archives"
const val COMPILE_CONSTANT = "compile"
const val IMPLEMENTATION_CONSTANT = "implementation"
const val API_CONSTANT = "api"
const val TEST_CONSTANT = "test"
const val RUNTIME_CONSTANT = "runtime"
const val PROVIDED_CONSTANT = "provided"
const val EXCLUSION_CONSTANT = "exclusion"
const val EXCLUSIONS_CONSTANT = "exclusions"
const val EXTENSION_CONSTANT = "extension"
const val DEPENDENCY_CONSTANT = "dependency"
const val DEPENDENCIES_CONSTANT = "dependencies"
const val VERSION_CONSTANT = "version"
const val EXPIRES_CONSTANT = "expires"
const val RAW_EXPIRES_DATE_CONSTANT = "rawExpiresDate"
const val GROUP_CONSTANT = "group"
const val NAME_CONSTANT = "name"
const val ARTIFACT_CONSTANT = "artifact"
const val SCOPE_CONSTANT = "scope"
const val ID_CONSTANT = "id"
const val SOURCES_CONSTANT = "sources"
const val PUBLISH_CONSTANT = "publish"
const val BUNDLE_CONSTANT = "bundle"
const val PUBLICATIONS_CONSTANT = "publications"
const val PUBLICATION_CONSTANT = "publication"
const val ALLOWLIST_CONSTANT = "whitelist"
const val HOURS_CONSTANT = "hours"
const val CONFIGURE_CONSTANT = "configure"
