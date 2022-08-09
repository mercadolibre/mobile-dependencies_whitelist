package com.mercadolibre.android.gradle.baseplugin.core.components

import com.mercadolibre.android.gradle.baseplugin.BasePlugin
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.AndroidConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.BasicsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ExtensionsConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.ModuleConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.configurers.PluginConfigurer
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.listProjects.AndroidProjectTypes
import com.mercadolibre.android.gradle.baseplugin.core.action.providers.RepositoryProvider
import com.mercadolibre.android.gradle.baseplugin.module.ModuleProvider
import com.mercadolibre.android.gradle.baseplugin.module.VersionProvider

/**
 * BASE.
 */
/** This variable contains a keyword used to set the resolution strategy time. */
const val RESOLUTION_STRATEGY_HOURS = 2

/** This variable contains a URL to config the build cache. */
const val BUILD_CACHE_URL = "https://gradle-ext.adminml.com/cache/"
/** This variable contains a keyword to config the build cache. */
const val BUILD_CACHE_CI = "CI"
/** This variable contains a keyword to config the build cache. */
const val BUILD_CACHE_CI_GRADLE_USER = "CI_GRADLE_USER"
/** This variable contains a keyword to config the build cache. */
const val BUILD_CACHE_CI_GRADLE_PASSWORD = "CI_GRADLE_USER_PASSWORD"

/** This variable contains the Allow List URL. */
const val ALLOW_LIST_URL = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json"

/** This variable contains a plugin. */
const val DEXCOUNT_PLUGIN = "com.getkeepsafe.dexcount"
/** This variable contains a plugin. */
const val BUGSNAG_PLUGIN = "com.bugsnag.android.gradle"
/** This variable contains a plugin. */
const val MAVEN_PUBLISH = "org.gradle.maven-publish"
/** This variable contains a plugin. */
const val JACOCO_PLUGIN = "org.gradle.jacoco"
/** This variable contains a plugin. */
const val KOTLIN_ANDROID = "kotlin-android"
/** This variable contains a plugin. */
const val LIBRARY_PLUGIN = "com.android.library"
/** This variable contains a plugin. */
const val APP_PLUGIN = "com.android.application"

/** This list contains the plugins that an library needs. */
val LIBRARY_PLUGINS = listOf(
    KOTLIN_ANDROID,
    LIBRARY_PLUGIN
)

/** This list contains the plugins that an app needs. */
val APP_PLUGINS = listOf(
    KOTLIN_ANDROID,
    APP_PLUGIN
)
/** This variable contains an CONSOLE style. */
const val WARNIGN_MESSAGE = "WARNING:"
/** This variable contains an CONSOLE style. */
const val ERROR_MESSAGE = "ERROR:"

/** This variable contains an ANSI style. */
const val ANSI_RESET = "\u001B[0m"
/** This variable contains an ANSI style. */
const val ANSI_GREEN = "\u001B[32m"
/** This variable contains an ANSI style. */
const val ANSI_YELLOW = "\u001B[33m"
/** This variable contains an ANSI style. */
const val ANSI_BLACK = "\u001B[30m"
/** This variable contains an ANSI style. */
const val ANSI_BOLD = "\u001B[0;1m"

/** This variable contains a string that styles the console reports. */
val SEPARATOR = "------------------------------------------------------------------------".ansi(ANSI_BLACK)
/** This variable contains a string that styles the console reports. */
const val ARROW = "-->"

/** This method is responsible for providing an ANSI style. */
infix fun String.ansi(ansiColor: String): String = ansiColor + this + ANSI_RESET

/** This method is responsible for providing an ANSI style. */
infix fun Int.ansi(ansiColor: String): String = ansiColor + this.toString() + ANSI_RESET

/**
 * These variables describe the behavior of the configurators.
 */

/** This variable is responsible for describing the operation of a Configurer. */
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

/** This variable is responsible for describing the operation of a Configurer. */
val BASICS_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${BasicsConfigurer::class.java.simpleName.ansi(ANSI_BOLD)} 
Responsible for configuring the meli proxy as well as managing cached versions.
- ${"Resolution Strategy by Hours".ansi(ANSI_YELLOW)} $ARROW ${RESOLUTION_STRATEGY_HOURS.ansi(ANSI_GREEN)}
- ${"Repositories".ansi(ANSI_YELLOW)}${"\n"}${RepositoryProvider().getRepositoriesDescription()}
""".trimIndent()

/** This variable is responsible for describing the operation of a Configurer. */
val EXTENSIONS_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${ExtensionsConfigurer::class.java.simpleName.ansi(ANSI_BOLD)}
Responsible for generating the necessary extensions to configure the modules.${"\n"}${ExtensionsConfigurer().getExtensions()}
""".trimIndent()

/** This variable is responsible for describing the operation of a Configurer. */
val MODULE_CONFIGURER_DESCRIPTION = """
$SEPARATOR
${ModuleConfigurer::class.java.simpleName}
Responsible for requesting the modules to configure their tasks within the projects.
- ${BasePlugin::class.java.simpleName.ansi(ANSI_YELLOW)}
${ModuleConfigurer().getModules("Java Modules", ModuleProvider.provideJavaModules())}
${ModuleConfigurer().getModules("Project Modules", ModuleProvider.provideProjectModules())}
${ModuleConfigurer().getModules("Settings Modules", ModuleProvider.provideSettingsModules())}
            
""".trimIndent()

/** This variable is responsible for describing the operation of a Configurer. */
val PLUGIN_CONFIGURER_DESCRITION = """
$SEPARATOR
${PluginConfigurer::class.java.simpleName.ansi(ANSI_BOLD)}
Responsible for providing the functionality to implement plugins in the project, also the basic plugins.
- ${PluginConfigurer::class.java.simpleName.ansi(ANSI_YELLOW)}        $ARROW ${ANSI_GREEN}apply function$ANSI_RESET
- ${"LibraryPluginConfigurer".ansi(ANSI_YELLOW)} $ARROW ${LIBRARY_PLUGINS.toString().replace("[", "").replace("]", "").ansi(ANSI_GREEN)}
- ${"AppPluginConfigurer".ansi(ANSI_YELLOW)}     $ARROW ${APP_PLUGINS.toString().replace("[", "").replace("]", "").ansi(ANSI_GREEN)}
""".trimIndent()

/** This variable is responsible for describing the operation of a Configurer. */
val ALL_PLUGIN_DESCRIPTION = """            
    
    
    
Meli Plugin of Gradle - Configs
$SEPARATOR
                
$ANDROID_CONFIGURER_DESCRIPTION
$BASICS_CONFIGURER_DESCRIPTION
$EXTENSIONS_CONFIGURER_DESCRIPTION
$PLUGIN_CONFIGURER_DESCRITION
""".trimIndent()

/**
 * These variables help modules express what they need.
 */

const val DEXCOUNT_PROPERTY = "enableDexcount" /** This variable contains the name of an property. */
const val BUGSNAG_EXTENSION = "bugsnagGradle" /** This variable contains the name of an extension. */
const val JACOCO_EXTENSION = "jacocoConfiguration" /** This variable contains the name of an extension. */
const val LINTABLE_EXTENSION = "lintGradle" /** This variable contains the name of an extension. */

const val MELI_GROUP = "meliPlugin" /** This variable contains a keyword used to set the group of a task. */
const val MELI_SUB_GROUP = "meliPluginSubTasks" /** This variable contains a keyword used to set the group of a task. */
const val JACOCO_GROUP = "reporting" /** This variable contains a keyword used to set the group of a task. */
const val JACOCO_VERIFICATION_GROUP = "verification" /** This variable contains a keyword used to set the group of a task. */
const val UNIT_TEST_GROUP = "verification" /** This variable contains a keyword used to set the group of a task. */
const val PUBLISHING_GROUP = "publishing" /** This variable contains a keyword used to set the group of a task. */
const val DOCUMENTATION_GROUP = "Documentation" /** This variable contains a keyword used to set the group of a task. */
const val PACKAGING_GROUP = "packaging" /** This variable contains a keyword used to set the group of a task. */
const val UNPACK_DEBUG_KEY_STORE_GROUP = "keystore" /** This variable contains a keyword used to set the group of a task. */

const val JACOCO_FULL_REPORT_TASK = "jacocoFullReport" /** This variable participates in the name of a Task. */
const val JACOCO_TEST_REPORT_TASK = "jacocoTestReport" /** This variable participates in the name of a Task. */
const val JACOCO_REPORT_FLAVOR_TEST_TASK_NAME = "jacocoTestvariantUnitTestReport" /** This variable participates in the name of a Task. */
const val TEST_TASK = "test" /** This variable participates in the name of a Task. */
const val LINTABLE_TASK = "lintGradle" /** This variable participates in the name of a Task. */
const val LINT_DEPENDENCIES_TASK = "lintDependencies" /** This variable participates in the name of a Task. */
const val LINT_RELEASE_DEPENDENCIES_TASK = "lintReleaseDependencies" /** This variable participates in the name of a Task. */
const val LIST_VARIANTS_TASK = "listVariants" /** This variable participates in the name of a Task. */
const val PROJECT_INFO_TASK = "projectInfo" /** This variable participates in the name of a Task. */
const val LIST_PROJECTS_TASK = "listProjects" /** This variable participates in the name of a Task. */
const val PLUGIN_DESCRIPTION_TASK = "pluginDescription" /** This variable participates in the name of a Task. */
const val APP_MODULE_PLUGIN_DESCRIPTION_TASK = "appPluginModuleDescription" /** This variable participates in the name of a Task. */
const val LIBRARY_MODULE_PLUGIN_DESCRIPTION_TASK = "libraryPluginModuleDescription" /** This variable participates in the name of a Task. */
const val LIBRARY_EXTENSION_PLUGIN_DESCRIPTION_TASK = "libraryPluginExtensionDescription" /** This variable participates in the name of a Task. */
const val APP_EXTENSION_PLUGIN_DESCRIPTION_TASK = "appPluginExtensionDescription" /** This variable participates in the name of a Task. */
const val PLUGIN_MODULES_DESCRIPTION_TASK = "pluginsModulesDescription" /** This variable participates in the name of a Task. */
const val TASK_GET_PROJECT_TASK = "getProjectVersion" /** This variable participates in the name of a Task. */
const val UNPACK_DEBUG_KEY_STORE_TASK = "unpackDebugKeystore" /** This variable participates in the name of a Task. */
const val PUBLISHING_JAVADOC_TASK = "Javadoc" /** This variable participates in the name of a Task. */
const val PUBLISHING_SOURCES_TASK = "Sources" /** This variable participates in the name of a Task. */
const val POM_FILE_TASK = "generatePomFileFor" /** This variable participates in the name of a Task. */
const val PUBLISHING_EXPERIMENTAL = "EXPERIMENTAL" /** This variable participates in the name of a Task. */
const val PUBLISHING_LOCAL = "LOCAL" /** This variable participates in the name of a Task. */
const val PUBLISHING_EXPERIMENTAL_SUBFIX_TASK = "$PUBLISHING_EXPERIMENTAL-" /** This variable participates in the name of a Task. */
const val PUBLISHING_LOCAL_SUBFIX_TASK = "$PUBLISHING_LOCAL-" /** This variable participates in the name of a Task. */
const val UNIT_TEST_FLAVOR_TEST_TASK_NAME = "testvariantUnitTest" /** This variable participates in the name of a Task. */

/** This variable participates in the description of a Task. */
const val JACOCO_REPORT_TASK_DESCRIPTION = "Generates Jacoco coverage reports for the build variant."

/** This variable participates in the description of a Task. */
const val JACOCO_TEST_REPORT_DESCRIPTION = "Generates Jacoco coverage reports"

/** This variable participates in the description of a Task. */
const val UNIT_TEST_TASK_DESCRIPTION = "Run unit tests for the build."

/** This variable participates in the description of a Task. */
const val LINTABLE_DESCRIPTION = "Lints the project dependencies to check they are in the allowed allowlist"

/** This variable participates in the description of a Task. */
const val LINT_WARNIGN_DESCRIPTION = "You should consider upgrading the lib OR contact the team owner to know how to proceed."

/** This variable participates in the description of a Task. */
const val LIST_VARIANTS_DESCRIPTION = "List all variant in this project"

/** This variable participates in the description of a Task. */
const val PROJECT_INFO_DESCRIPTION = "List project configurations"

/** This variable participates in the description of a Task. */
const val LIST_PROJECTS_DESCRIPTION = "List all subprojects in this project"

/** This variable participates in the description of a Task. */
const val PLUGIN_DESCRIPTION_DESCRIPTION = "Describes the settings managed by the plugin"

/** This variable participates in the description of a Task. */
const val TASK_GET_PROJECT_DESCRIPTION = "Gets project version"

/** This variable participates in the description of a Task. */
const val UNPACK_DEBUG_KEY_STORE_DESCRIPTION = "Unpack the debug keystore into the build directory of the project"

/** This variable participates in the description of a Task. */
const val PUBLISHING_JAVADOC_DESCRIPTION = "Generates Javadoc for"

/** This variable configure the retry convention of build cache. */
const val BUGSNAG_RETRY_CONVENTION = 5

/**
 * JACOCO.
 */

/** This variable contains values necessary for the operation of a module. */
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
    "**/*$*$*.*",
    "jdk.internal.*"
)

/**
 * LINT.
 */

/** This variable contains values necessary for the operation of a module. */
const val LINT_ERROR_TITLE = "ERROR: The following dependencies are not allowed:"

/** This variable contains values necessary for the operation of a module. */
const val LINT_WARNIGN_TITLE = "WARNING: The following dependencies has been marked as deprecated:"

/** This variable contains values necessary for the operation of a module. */
const val LINT_TASK_FAIL_MESSAGE = "Errors found while running lints, please check the console output for more information"

/** This variable contains values necessary for the operation of a module. */
const val LINT_ERROR_ALLOWED_DEPENDENCIES_PREFIX = "\nYour project can only contain the dependencies listed in: URL \n" +
    "If you think one of them should be in the allowlist, please start here " +
    "https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas\n"

/** This variable contains values necessary for the operation of a module. */
const val LINT_FILENAME = "lint.ld"

/** This variable contains values necessary for the operation of a module. */
const val LINT_WARNING_FILENAME = "lintWarning.ld"

/** This variable contains values necessary for the operation of a module. */
const val LINT_AVAILABLE = "available"

/** This variable contains values necessary for the operation of a module. */
const val LINT_INVALID = "invalid"

/** This variable contains values necessary for the operation of a module. */
const val LINT_EXPIRED = "expired"

/** This variable contains values necessary for the operation of a module. */
const val LINT_GOING_TO_EXPIRE = "going to expire"

/** This variable contains values necessary for the operation of a module. */
const val LINT_REPORT_ERROR = "Cant report this type of dependency"

/** This variable contains values necessary for the operation of a module. */
const val LINT_RELEASE_ERROR_TITLE = "Error. Found non-release dependencies in the module release version:"

/** This variable contains values necessary for the operation of a module. */
val LINT_LIBRARY_FILE_BLOCKER = "build/reports/LibraryAllowListDependenciesLint/$LINT_FILENAME"

/** This variable contains values necessary for the operation of a module. */
val LINT_LIBRARY_FILE_WARNING = "build/reports/LibraryAllowListDependenciesLint/$LINT_WARNING_FILENAME"

/** This variable contains values necessary for the operation of a module. */
val LINT_RELEASE_FILE = "build/reports/ReleaseDependenciesLint/$LINT_FILENAME"

/**
 * LIST PROJECTS.
 */

/** This variable contains values necessary for the operation of a module. */
const val BEGINNING_TOKEN = "=== BEGINNING OF PROJECTS LIST ==="

/** This variable contains values necessary for the operation of a module. */
val TYPE_NOT_RECOGNISED_MESSAGE = "Specified project type not recognised. Project types available are " + AndroidProjectTypes.values()

/** This variable contains values necessary for the operation of a module. */
const val FILE_NAME_PROJECT_VERSION = "project.version"

/**
 * Repositories
 */

/** This variable contains the name of a repository. */
const val INTERNAL_EXPERIMENTAL = "AndroidInternalExperimental"
/** This variable contains the name of a repository. */
const val INTERNAL_RELEASES = "AndroidInternalReleases"
/** This variable contains the name of a repository. */
const val EXTERNAL_RELEASES = "AndroidExternalReleases"
/** This variable contains the name of a repository. */
const val PUBLIC_RELEASES = "AndroidPublicReleases"
/** This variable contains the name of a repository. */
const val ANDROID_EXTRA = "AndroidExtra"
/** This variable contains the url of a repository. */
const val INTERNAL_EXPERIMENTAL_URL = "https://android.artifacts.furycloud.io/repository/experimental/"
/** This variable contains the url of a repository. */
const val INTERNAL_RELEASES_URL = "https://android.artifacts.furycloud.io/repository/releases/"
/** This variable contains the url of a repository. */
const val PUBLIC_AND_EXTERNAL_RELEASES_URL = "https://artifacts.mercadolibre.com/repository/android-releases/"
/** This variable contains the url of a repository. */
const val ANDROID_EXTRA_URL = "https://android.artifacts.furycloud.io/repository/extra/"

/** This variable contains the package of a repository. */
const val MERCADOLIBRE_PACKAGE = "com\\.mercadolibre\\..*"
/** This variable contains the package of a repository. */
const val MERCADOPAGO_PACKAGE = "com\\.mercadopago\\..*"
/** This variable contains the package of a repository. */
const val MERCADOENVIOS_PACKAGE = "com\\.mercadoenvios\\..*"

/** This variable contains a regex. */
const val REGEX = ".*"
/** This variable contains a regex. */
const val PUBLISH_REGEX = "^((?!EXPERIMENTAL-|LOCAL-).)*$"
/** This variable contains a regex. */
const val PUBLISH_LOCAL_REGEX = "^(.*-)?LOCAL-.*$"
/** This variable contains a regex. */
const val PUBLISH_EXPERIMENTAL_REGEX = "^(.*-)?EXPERIMENTAL-.*$"

/**
 * KEY STORE.
 */
const val DIRECTORY_NAME = "keystores" /** This variable contains values necessary for the operation of a module. */
const val FILE_NAME_DEBUG_KEY = "debug_keystore" /** This variable contains values necessary for the operation of a module. */
const val KEY_STORE_PASSWORD = "android" /** This variable contains values necessary for the operation of a module. */
const val KEY_ALIAS = "androiddebugkey" /** This variable contains values necessary for the operation of a module. */
const val KEY_PASSWORD = "android" /** This variable contains values necessary for the operation of a module. */

/**
 * PUBLISHING.
 */
const val PUBLISHING_TIME_GENERATOR = "yyyyMMddHHmmss" /** This variable contains values necessary for the operation of a module. */
const val PUBLISHING_PRINT_MESSAGE = "Publishing version: " /** This variable contains values necessary for the operation of a module. */
const val PUBLISHING_TIME_ZONE = "UTC" /** This variable contains values necessary for the operation of a module. */
const val PUBLISHING_MAVEN_LOCAL = "MavenLocal" /** This variable contains values necessary for the operation of a module. */
const val SOURCE_SETS_TEST = "test"/** This variable contains values necessary for the operation of a module. */
const val SOURCE_SETS_DEFAULT = "main" /** This variable contains values necessary for the operation of a module. */
const val JAVA_COMPILE_PROVIDER = "javaCompileProvider" /** This variable contains values necessary for the operation of a module. */
const val PACKAGE_LIBRARY_PROVIDER = "packageLibraryProvider" /** This variable contains values necessary for the operation of a module. */
const val PUBLISHING_POM_FILE = "pom-default.xml" /** This variable contains values necessary for the operation of a module. */
const val TASK_TYPE_RELEASE = "Release" /** This variable contains values necessary for the operation of a module. */
const val TASK_TYPE_EXPERIMENTAL = "Experimental" /** This variable contains values necessary for the operation of a module. */
const val TASK_TYPE_LOCAL = "Local" /** This variable contains values necessary for the operation of a module. */
const val TASK_TYPE_PUBLIC_RELEASE = "PublicRelease" /** This variable contains values necessary for the operation of a module. */
const val TASK_TYPE_PRIVATE_RELEASE = "PrivateRelease" /** This variable contains values necessary for the operation of a module. */

/**
 * GRADLE ENTERPRISE.
 */

const val GRADLE_ENTERPRISE_SERVICES_URL = "https://gradle.com/terms-of-service" /** This variable contains values necessary for the operation of a module. */
const val GRADLE_ENTERPRISE_SERVER_URL = "https://gradle.adminml.com/" /** This variable contains values necessary for the operation of a module. */
const val GRADLE_ENTERPRISE_SERVICES_AGREE = "yes" /** This variable contains values necessary for the operation of a module. */
const val GIT_BRANCH = "Git branch" /** This variable contains values necessary for the operation of a module. */
const val GIT_COMMIT = "Git Commit ID" /** This variable contains values necessary for the operation of a module. */
const val GIT_USER_NAME = "user_name" /** This variable contains values necessary for the operation of a module. */
const val GIT_EMAIL = "user_email" /** This variable contains values necessary for the operation of a module. */
const val GIT_REMOTE_URL = "remote_url" /** This variable contains values necessary for the operation of a module. */
const val COMMAND_COMMIT = "git rev-parse --verify HEAD" /** This variable contains values necessary for the operation of a module. */
const val COMMAND_BRANCH = "git rev-parse --abbrev-ref HEAD" /** This variable contains values necessary for the operation of a module. */
const val COMMAND_USER_NAME = "git config user.name" /** This variable contains values necessary for the operation of a module. */
const val COMMAND_EMAIL = "git config user.email" /** This variable contains values necessary for the operation of a module. */
const val COMMAND_REMOTE_URL = "git config --get remote.origin.url" /** This variable contains values necessary for the operation of a module. */

/** This variable contains values necessary for the operation of a module. */
val PUBLISHING_EXCLUDES_ARR = mutableListOf(
    "**/BuildConfig.java",
    "**/R.java"
)

/** This variable contains values necessary for the operation of a module. */
val PUBLISHING_LINKS_ARR = mutableListOf(
    "http://docs.oracle.com/javase/7/docs/api/",
    "http://d.android.com/reference/"
)

/** This variable contains values necessary for the operation of a module. */
val PUBLISHING_LINKS_JAR = mutableListOf(
    "http://docs.oracle.com/javase/7/docs/api/"
)

/** This variable contains values necessary for the operation of a module. */
val PUBLISHING_OPTIONS = mutableMapOf(
    "Xdoclint:none" to "-quiet"
)

/**
 * These variables are keywords.
 */

const val ANDROID_USER_NAME = "AndroidInternalReleasesUsername" /** This variable contains a keyword used to get credentials. */
const val ANDROID_USER_PASSWORD = "AndroidInternalReleasesPassword" /** This variable contains a keyword used to get credentials. */
const val CI_CONSTANT = "CI" /** This variable contains a keyword used to get credentials. */
const val LOCAL_CONSTANT = "Local" /** This variable contains a keyword used to get credentials. */
const val RELEASE_CONSTANT = "release" /** This variable contains a keyword used to generate text statements. */
const val MDS_CONSTANT = "mds" /** This variable contains a keyword used to generate text statements. */
const val DEBUG_CONSTANT = "debug" /** This variable contains a keyword used to generate text statements. */
const val FLAVOR_CONSTANT = "flavor" /** This variable contains a keyword used to generate text statements. */
const val VARIANT_CONSTANT = "variant" /** This variable contains a keyword used to generate text statements. */
const val BUILD_CONSTANT = "build" /** This variable contains a keyword used to generate text statements. */
const val PACKAGING_AAR_CONSTANT = "Aar" /** This variable contains a keyword used to generate text statements. */
const val PACKAGING_JAR_CONSTANT = "Jar" /** This variable contains a keyword used to generate text statements. */
const val DIR_CONSTANT = "dir" /** This variable contains a keyword used to generate text statements. */
const val EXCLUDES_CONSTANT = "excludes" /** This variable contains a keyword used to generate text statements. */
const val TYPE_CONSTANT = "type" /** This variable contains a keyword used to generate text statements. */
const val DEFAULT_CONSTANT = "default" /** This variable contains a keyword used to generate text statements. */
const val ARCHIVES_CONSTANT = "archives" /** This variable contains a keyword used to generate text statements. */
const val COMPILE_CONSTANT = "compile" /** This variable contains a keyword used to generate text statements. */
const val IMPLEMENTATION_CONSTANT = "implementation" /** This variable contains a keyword used to generate text statements. */
const val CLASSPATH_CONSTANT = "classpath" /** This variable contains a keyword used to generate text statements. */
const val API_CONSTANT = "api" /** This variable contains a keyword used to generate text statements. */
const val TEST_CONSTANT = "test" /** This variable contains a keyword used to generate text statements. */
const val RUNTIME_CONSTANT = "runtime" /** This variable contains a keyword used to generate text statements. */
const val PROVIDED_CONSTANT = "provided" /** This variable contains a keyword used to generate text statements. */
const val EXCLUSION_CONSTANT = "exclusion" /** This variable contains a keyword used to generate text statements. */
const val EXCLUSIONS_CONSTANT = "exclusions" /** This variable contains a keyword used to generate text statements. */
const val EXTENSION_CONSTANT = "extension" /** This variable contains a keyword used to generate text statements. */
const val DEPENDENCY_CONSTANT = "dependency" /** This variable contains a keyword used to generate text statements. */
const val DEPENDENCIES_CONSTANT = "dependencies" /** This variable contains a keyword used to generate text statements. */
const val VERSION_CONSTANT = "version" /** This variable contains a keyword used to generate text statements. */
const val EXPIRES_CONSTANT = "expires" /** This variable contains a keyword used to generate text statements. */
const val RAW_EXPIRES_DATE_CONSTANT = "rawExpiresDate" /** This variable contains a keyword used to generate text statements. */
const val GROUP_CONSTANT = "group" /** This variable contains a keyword used to generate text statements. */
const val NAME_CONSTANT = "name" /** This variable contains a keyword used to generate text statements. */
const val ARTIFACT_CONSTANT = "artifact" /** This variable contains a keyword used to generate text statements. */
const val SCOPE_CONSTANT = "scope" /** This variable contains a keyword used to generate text statements. */
const val ID_CONSTANT = "id" /** This variable contains a keyword used to generate text statements. */
const val SOURCES_CONSTANT = "sources" /** This variable contains a keyword used to generate text statements. */
const val PUBLISH_CONSTANT = "publish" /** This variable contains a keyword used to generate text statements. */
const val BUNDLE_CONSTANT = "bundle" /** This variable contains a keyword used to generate text statements. */
const val PUBLICATIONS_CONSTANT = "publications" /** This variable contains a keyword used to generate text statements. */
const val PUBLICATION_CONSTANT = "publication" /** This variable contains a keyword used to generate text statements. */
const val ALLOWLIST_CONSTANT = "whitelist" /** This variable contains a keyword used to generate text statements. */
const val HOURS_CONSTANT = "hours" /** This variable contains a keyword used to generate text statements. */
const val CONFIGURE_CONSTANT = "configure" /** This variable contains a keyword used to generate text statements. */
const val JSON_CONSTANT = "json" /** This variable contains a keyword used to generate text statements. */
