MercadoLibre Gradle Android Plugins for Gradle
==============================

## What is this?
This project includes three Gradle plugins to make the Android library developer's life easier:

1. library
2. application
3. robolectric [incubating]
4. jacoco [incubating]
3. base

## What does the library plugin do for us?

This plugin add new tasks to the Gradle build script that applies them (except the base plugin). The goal of these tasks is to test and publish Android libraries to Maven repositories, but running some other important tasks before:

1. Generate JAR containing source code per each Android variant.
2. Generate HTMLs with all the related Javadoc per each Android variant (turned off in the latest versions).
3. Generate JAR containing the Javadoc's HTMLs (turned off in the latest versions).
4. Create lint reports (only if publishing as release - see below).
5. Run Android (Instrumentation) tests.
6. Run JUnit tests.
7. Create Jacoco reports. **[using Jacoco Plugin]**
8. Create Robolectric necesary files to work. **[using Robolectric Plugin]**
9. Upload the artifacts (library .aar, sources and Javadoc) to the Maven repository, depending on what task you run (see below).

In order to get all of this done, the plugins add new tasks to your build script, so that you can call them with the Gradle command line tool.

## Tasks added by library plugin

1. `publishAarRelease` - Publishes the .aar (along with the sources and Javadoc) to the specified Maven releases repository. It runs all the checks before uploading the artifacts (lint, tests, etc.), and once they get uploaded, it tags the version in Git.
2. `publishAarExperimental` - Same as `publishAarRelease`, but it uploads the artifacts to the specified Maven experimental repository. It does NOT tag the version in Git, as it is not a release.
3. `publishAarLocal` - This is particularly useful during the development phase of the Android library. It overwrites the artifacts in your .m2/repository directory, so that you can code & test your code without uploading anything to a remote repository. This does NOT run lint so that we don't have to wait that long. It does not tag the version in Git either.

## How to add library plugin to your project?

Example: [mobile-android_commons](https://github.com/mercadolibre/mobile-android_commons)

Simple. You just need to apply the plugin and configure it in the build script, as the following snippet shows:

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.
        maven {
            url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases' // Releases URL.
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.mercadolibre.android.gradle:base:1.5'
        classpath 'com.mercadolibre.android.gradle:library:X.X'
        //Necesary for library plugin
        classpath 'com.mercadolibre.android.gradle:jacoco:X.X'
        //Necesary for library plugin
        classpath 'com.mercadolibre.android.gradle:robolectric:X.X'
    }
}
```
**Your module's build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.library'

android {
    compileSdkVersion 21
    buildToolsVersion "21.1.2"

    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

        debug {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

        testing {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

publisher.releasesRepository.url = [YOUR MAVEN RELEASES REPO URL]
publisher.releasesRepository.username = [YOUR USERNAME]
publisher.releasesRepository.password = [YOUR PASSWORD]

publisher.experimentalRepository.url = [YOUR MAVEN SNAPSHOTS REPO URL]
publisher.experimentalRepository.username = [YOUR USERNAME]
publisher.experimentalRepository.password = [YOUR PASSWORD]

publisher.groupId = [YOUR GROUPID FOR MAVEN]
publisher.artifactId = project.name // Or whatever...
publisher.version = [YOUR LIBRARY VERSION]

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:21.0.3'
}
```    
As you can see, there is no need to apply the _com.android.library_ nor the _maven_ plugins, as they are automatically applied by the aar-publisher plugin.

## What does the jacoco plugin do for us?

This plugin adapts Java's Jacoco plugin to Android. It aims to provide test code coverage for new Android Unit Testing feature that Android Jacoco Plugin does not contemplate. 

## Tasks added by jacoco plugin

1. `jacoco{buildType}` - This task creates a Jacoco Code Coverage Report for specified buildType. *I.E.: jacocoDebug*. This task DOES NOT assemble your variant because of building times performance, you should assemble first desired variant.
 
**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.
        maven {
            url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases' // Releases URL.
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.mercadolibre.android.gradle:base:1.5'

        classpath 'com.mercadolibre.android.gradle:jacoco:X.X'
    }
}
```
**Your module's build.gradle**
```groovy
apply plugin: 'com.android.library' //Or application
apply plugin: 'com.mercadolibre.android.gradle.jacoco'

.....
```    

## What does the robolectric plugin do for us?

This plugin creates all necesary files to make Robolectric run using library dependencies. It also provides to Android Unit Testing libraries a mechanism to register R auto-generated classes because Android's library plugin does not provide it.

***If you are working on an Android library**, Android Gradle Plugin will not auto-generate R classes for Unit Testing variants, so you will not be able to create tests asserting resources. This plugin fixes this issue, you only need to create a **'gradle.properties'** file in your library root folder. Inside this specify an "exampleApp" property indicating your test application name. This will let Android plugin to obtain needed R auto-generated files.

## Tasks added by robolectric plugin

1. `createRobolectricFiles` - This task creates 'project.properties' and 'test-project.properties' files needed by Robolectric lib to work properly with resourcefull dependencies.
2. `cleanRobolectricFiles` - This task is hooked to `clean` tasks and it's purpose is to delete previous mentioned files to avoid cache issues
 
**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.
        maven {
            url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases' // Releases URL.
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.mercadolibre.android.gradle:base:1.5'

        classpath 'com.mercadolibre.android.gradle:robolectric:X.X'
    }
}
```
**Your module's build.gradle**
```groovy
apply plugin: 'com.android.library' //Or application
apply plugin: 'com.mercadolibre.android.gradle.robolectric'

.....

testCompile 'org.robolectric:robolectric:X.X'
```    
## What does the application plugin do for us?

At this moment, **application plugin is only a wrapper for Robolectric and Jacoco plugins**. It's purpose is to contain all application refered tasks to avoid copy and paste in all app projects.

## What does the base plugin do for us?

This plugin helps us on the following things:

1. It configures the custom Nexus repositories when using our custom Android Libraries as dependencies in Gradle.
2. It links the sources of the dependencies, for the two possible packagings (JARs and AARs).

All you have to do is:

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This is the plugin!

buildscript {
    repositories {
        jcenter()
        maven {
            url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases'
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.mercadolibre.android.gradle:base:1.5' // Let Gradle know that we are going to need this plugin.
    }
}
```   

**Your module's build.gradle**
```groovy
dependencies {
    // The following dependencies are just examples! Notice that we are using a wildcard to always get the latest EXPERIMENTAL artifact from particular versions. The Base plugin handles the Gradle cache for us, so that this module is always pointing to the latest EXPERIMENTAL dependencies.
    compile ('com.mercadolibre.android:networking:0.0.1-EXPERIMENTAL-+')
    compile ('com.mercadolibre.android:commons:0.1.0-EXPERIMENTAL-+')
}
```   

## How to improve or compile the plugins?
If you want to improve MercadoLibre Gradle plugins, you should follow these steps:

1. Clone the project.
2. Import the project from IntelliJ Community Edition (not Android Studio!) (NOTE: Import it, do not 'open' it). You MUST select the root build.gradle when importing the project, otherwise IntelliJ will not recognize all the Gradle modules. 
3. Make sure you have a JDK configured for the project: _File -> Project Structure -> Project SDK_.
4. Make the changes you need.
5. Publish a new version of the plugins to the plugins repository, with any of these methods:
    1. Locally (on your .m2/repository directory): `./gradlew library:install` or `./gradlew base:install`
    2. Remotelly (on Nexus): `./gradlew library:uploadArchives` or `./gradlew base:uploadArchives` - You can check if the plugin has been uploaded by browsing [Nexus](http://maven-mobile.melicloud.com/nexus/content/repositories/). If you want to publish as a snapshot, make sure the version ends with "-SNAPSHOT", otherwise it will get uploaded as a release. See the inner build.gradle to modify the version.

## Possible errors with library plugin

- If you get a 400 HTTP error while uploading the artifacts, make sure you are not trying to publish an existing release version. For instance, Nexus repository refuses to save an artifact in the release directory if the version already exists.
- Throws an exception when a local dependency is found in the build.gradle file, to prevent publishing invalid artifacts.

## Changelog

### library plugin
- 1.1:
 -  Robolectric and jacoco tasks were deacopled from this plugin. Now includes both by default.
- 1.0: 
 - `jacoco{buildType}` tasks added. One per build type will be created. 
 - Inherits **all deprecated aar-publisher features.**

### jacoco plugin
- 1.0:
 - Jacoco tasks working standalone.

### robolectric plugin
- 1.0:
 - Robolectric tasks working standalone.

### jar-publisher  <span style="color:red">**DEPRECATED**</span>

- Removed from repository, out of maintainence
- 1.1: Removed `publishJarSnapshot`. Added `publishJarExperimental`.
- 1.0: First version of the plugin!

### aar-publisher  <span style="color:red">**DEPRECATED**</span>

- 1.3: Prevents to have local dependencies declared in your build.gradle (like `compile project(':anotherProject')`), as this way is invalid for published artifacts.
- 1.2: Bugfixing. Turned off javadoc generation as it is currently working bad.
- 1.1: Removed `publishAarSnapshot`. Added `publishAarExperimental`.
- 1.0: First version of the plugin!

### base

- 1.5: Fixed bug: the plugin is not attaching the sources when pointing to a LOCAL version.
- 1.4: It now attaches the sources for 'provided' dependencies (JARs and AARs) (although you should not use 'provided' in any case...).
- 1.3: It now attaches the sources for 'compile' dependencies (JARs and AARs). It does not work with 'provided' dependencies (next version).
- 1.2: Added mavenLocal() as default repository.
- 1.1: Added mavenCentral() as default repository.
- 1.0: First version of the plugin!

## Further help
If you need further help, please contact [martin.heras@mercadolibre.com](mailto:martin.heras@mercadolibre.com) or [mobile-it@mercadolibre.com](mailto:mobile-it@mercadolibre.com).
