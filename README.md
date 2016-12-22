MercadoLibre Gradle Android Plugins for Gradle
==============================

## Repo status

- Branch: **master**

[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![Build Status](https://magnum.travis-ci.com/mercadolibre/mobile-android_gradle.svg?token=miquMjuW9qs6Ssw13jPd&branch=master)](https://magnum.travis-ci.com/mercadolibre/mobile-android_gradle)

- Branch: **develop**

[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![Build Status](https://magnum.travis-ci.com/mercadolibre/mobile-android_gradle.svg?token=miquMjuW9qs6Ssw13jPd&branch=develop)](https://magnum.travis-ci.com/mercadolibre/mobile-android_gradle)

## What is this?
This project includes three Gradle plugins to make the Android library developer's life easier:

1. application
2. base
3. library
4. jacoco
5. robolectric


## Repo usage
*Do not clone or push to* **master** *branch.*

Create branch or fork from **develop**, then push or create pull requests (if you don't have access) to that branch.

The repo uses [this branching model](http://nvie.com/posts/a-successful-git-branching-model/).


## What does the library plugin do for us?

This plugin add new tasks to the Gradle build script that applies them (except the base plugin). The goal of these tasks is to test and publish Android libraries to Maven repositories, but running some other important tasks before:

1. Generate JAR containing source code per each Android variant.
2. Generate HTMLs with all the related Javadoc per each Android variant (turned off in the latest versions).
3. Generate JAR containing the Javadoc's HTMLs (turned off in the latest versions).
4. Create lint reports (only if publishing as release - see below).
5. Run Android (Instrumentation) tests.
6. Run JUnit tests.
7. Create Jacoco reports. **[using Jacoco Plugin]**
8. Create Robolectric necessary files to work. **[using Robolectric Plugin]**
9. Upload the artifacts (library .aar, sources and Javadoc) to the Maven repository, depending on what task you run (see below).

In order to get all of this done, the plugins add new tasks to your build script, so that you can call them with the Gradle command line tool.

### Tasks added by library plugin

1. `publishAarRelease` - Publishes the .aar (along with the sources and Javadoc) to Bintray releases repository. It runs all the checks before uploading the artifacts (lint, tests, etc.), and once they get uploaded, it tags the version in Git.
2. `publishAarExperimental` - Same as `publishAarRelease`, but it uploads the artifacts to the specified Maven experimental repository. It does NOT tag the version in Git, as it is not a release.
3. `publishAarAlpha` - Same as `publishAarRelease`, but it uploads the artifacts to the specified Maven alpha repository. This is a release, but in alpha.
4. `publishAarLocalXXXX` - This is particularly useful during the development phase of the Android library. It overwrites the artifacts in your .m2/repository directory, so that you can code & test your code without uploading anything to a remote repository. This does NOT run lint so that we don't have to wait that long. It does not tag the version in Git either. 
Note: XXXX is the build variant that you want to run for the publish. In most cases it will probably be Releease or Debug.
An example would be: `./gradlew project:publishAarLocalRelease`

## How to add library plugin to your project?

Example: [mobile-android_commons](https://github.com/mercadolibre/mobile-android_commons)

Simple. You just need to apply the plugin and configure it in the build script, as the following snippet shows:

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.
        //Releases Bintray Configuration
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'
        classpath 'com.mercadolibre.android.gradle:base:3.+'
        classpath 'com.mercadolibre.android.gradle:library:3.+'

        // Necessary for library plugin
        classpath 'com.mercadolibre.android.gradle:jacoco:3.+'
        classpath 'com.mercadolibre.android.gradle:robolectric:3.+'
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
As you can see, there is no need to apply the _com.android.library_ nor the _maven_ plugins, as they are automatically applied by the library plugin.

## What does the jacoco plugin do for us?

This plugin adapts Java's Jacoco plugin to Android. It aims to provide test code coverage for new Android Unit Testing feature that Android Jacoco Plugin does not contemplate. 

### Tasks added by jacoco plugin

1. `jacoco{buildType}` - This task creates a Jacoco Code Coverage Report for specified buildType. *I.E.: jacocoDebug*. This task DOES NOT assemble your variant because of building times performance, you should assemble first desired variant.
 
**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.

        // New Maven Bintray Repository
        maven {
            url  "https://dl.bintray.com/mercadolibre/android-releases"
            credentials {
                username 'bintray-read'
                password 'ff5072eaf799961add07d5484a6283eb3939556b'
        }
    }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'
        classpath 'com.mercadolibre.android.gradle:base:3.+'

        classpath 'com.mercadolibre.android.gradle:jacoco:3.+'
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

This plugin creates all necessary files to make Robolectric run using library dependencies. It also provides to Android Unit Testing libraries a mechanism to register R auto-generated classes because Android's library plugin does not provide it.

***If you are working on an Android library**, Android Gradle Plugin will not auto-generate R classes for Unit Testing variants, so you will not be able to create tests asserting resources. This plugin fixes this issue, you only need to create a **'gradle.properties'** file in your library root folder. Inside this specify an "exampleApp" property indicating your test application name. This will let Android plugin to obtain needed R auto-generated files.

### Tasks added by robolectric plugin

1. `createRobolectricFiles` - This task creates 'project.properties' and 'test-project.properties' files needed by Robolectric lib to work properly with resourcefull dependencies.
2. `cleanRobolectricFiles` - This task is hooked to `clean` tasks and it's purpose is to delete previous mentioned files to avoid cache issues
 
**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.

        // New Maven Bintray Repository
        maven {
            url  "https://dl.bintray.com/mercadolibre/android-releases"
            credentials {
                username 'bintray-read'
                password 'ff5072eaf799961add07d5484a6283eb3939556b'
        }
    }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'
        classpath 'com.mercadolibre.android.gradle:base:3.+'

        classpath 'com.mercadolibre.android.gradle:robolectric:3.+'
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

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.

        // New Maven Bintray Repository
        maven {
            url  "https://dl.bintray.com/mercadolibre/android-releases"
            credentials {
                username 'bintray-read'
                password 'ff5072eaf799961add07d5484a6283eb3939556b'
        }
    }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'

		classpath("com.mercadolibre.android.gradle:base:3.+")
        classpath("com.mercadolibre.android.gradle:application:3.+")
        classpath("com.mercadolibre.android.gradle:jacoco:3.+")
        classpath("com.mercadolibre.android.gradle:robolectric:3.+")
    }
}
```

**Your module's build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.application'

.....
```    

## What does the base plugin do for us?

This plugin helps us on the following things:

1. It configures the custom Bintray repositories when using our custom Android Libraries as dependencies in Gradle.
2. It links the sources of the dependencies, for the two possible packagings (JARs and AARs).

All you have to do is:

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This is the plugin!

buildscript {
    repositories {
        jcenter()
        // New Maven Bintray Repository
        maven {
            url  "https://dl.bintray.com/mercadolibre/android-releases"
            credentials {
                username 'bintray-read'
                password 'ff5072eaf799961add07d5484a6283eb3939556b'
        }
    }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'
		
		// Let Gradle know that we are going to need this plugin.
        classpath 'com.mercadolibre.android.gradle:base:3.+'
    }
}
```   

**Your module's build.gradle**
```java
dependencies {
    // The following dependencies are just examples! Notice that we are using a wildcard to always get the latest EXPERIMENTAL artifact from particular versions. The Base plugin handles the Gradle cache for us, so that this module is always pointing to the latest EXPERIMENTAL dependencies.
    compile ('com.mercadolibre.android:networking:0.0.1-EXPERIMENTAL-+')
    compile ('com.mercadolibre.android:commons:0.1.0-EXPERIMENTAL-+')
}
```   

### Tasks added by library plugin

2. Create `jacocoFullReport` task which merges subproject's JaCoCo reports into one.
3. Create `coveralls` task which post the previously created JaCoCo report to Coveralls.io

## Contributing

### Base flow
If you want to improve MercadoLibre Gradle plugins, you should follow these steps:

1. Clone the project.
2. Import the project from IntelliJ Community Edition (not Android Studio!) (NOTE: Import it, do not 'open' it). You MUST select the root build.gradle when importing the project, otherwise IntelliJ will not recognize all the Gradle modules. 
3. Make sure you have a JDK configured for the project: _File -> Project Structure -> Project SDK_.
4. Make the changes you need.
5. Publish a new version of the plugins locally (on your .m2/repository directory): `./gradlew library:install` or `./gradlew base:install`

### PRO tip

1. Make some changes
2. Compile them by doing: `./gradlew assemble`
2. In the root `build.gradle` under `buildscript.dependencies` of the repo you want to test replace the library with your local classes directory: 
    ```
    classpath files('.../build/classes/main', '.../build/resources/main')
    ```
3. Test it!

### Publish
Travis will publish each module after any merged PR on the **master** branch when the latest commit contains the flag: `[ci deploy]`.

To be able to do this, be sure to always keep both `bintray.properties` and `Rakefile` ignored in your `.gitignore` file.

If you want to publish them manually, you need to setup either a properties file `bintray.properties` containing:
- `bintray.user`
- `bintray.key`

or the following environment variables:
- `BINTRAY_USER`
- `BINTRAY_KEY`

To publish it remotely (on Bintray) just run: `./gradlew base:uploadArchives` - You can check if the plugin has been uploaded by browsing [Nexus](http://maven-mobile.melicloud.com/nexus/content/repositories/). If you want to publish as a snapshot, make sure the version ends with "-SNAPSHOT", otherwise it will get uploaded as a release. See the inner build.gradle to modify the version.

## Possible errors with library plugin

- If you get a 400 HTTP error while uploading the artifacts, make sure you are not trying to publish an existing release version. For instance, Nexus repository refuses to save an artifact in the release directory if the version already exists.
- Throws an exception when a local dependency is found in the build.gradle file, to prevent publishing invalid artifacts.

## Migration Guide from library 1.1 to 1.+

The gradle library plugin version 1.2 includes publication to Bintray instead of maven-mobile. Because of this new 
classpath need to be added and repositories.

Example: [mobile-android_ui](https://github.com/mercadolibre/mobile-android_ui/tree/develop)

You should now use the "+" wildcard to include our gradle plugin dependencies in order to avoid
being constantly updating the plugin (check how the base, library, jacoco and robolectric dependencies
are included below).

**Parent build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.base' // This sets up our custom Nexus repositories. It is also important because it turns off the Gradle cache for dynamic versions.

buildscript {
    repositories { // This repositories are used when building your project. In this case, we need to tell Gradle to use our repositories in order to find the Gradle Publisher plugins.
        jcenter() // This is needed by Gradle.
        
        // New Maven Bintray Repository
        maven {
            url  "https://dl.bintray.com/mercadolibre/android-releases"
            credentials {
                username 'bintray-read'
                password 'ff5072eaf799961add07d5484a6283eb3939556b'
            }
        }
    }
    
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        
        classpath 'com.mercadolibre.android.gradle:base:1.+'
        classpath 'com.mercadolibre.android.gradle:library:1.+'
        
        // Necessary for application plugin
        classpath 'com.mercadolibre.android.gradle:jacoco:1.+'
        classpath 'com.mercadolibre.android.gradle:robolectric:1.+'
        
        // New classpath to be added for Bintray
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.0'
        classpath 'com.github.dcendents:android-maven-plugin:1.2'
    }
}
```

**Using Gradle 2.4+**

If you intend to  use Gradle 2.4+, the `com.github.dcendents:android-maven-plugin:1.2`
dependency should be changed to `com.github.dcendents:android-maven-gradle-plugin:1.3`

Be sure to configure the `wrapper` task accordingly.


**Your module's build.gradle**
You don't need the releases nor experimental configurations anymore (remove them).

    publisher.releasesRepository.url = [YOUR MAVEN RELEASES REPO URL]
    publisher.releasesRepository.username = [YOUR USERNAME]
    publisher.releasesRepository.password = [YOUR PASSWORD]
    
    publisher.experimentalRepository.url = [YOUR MAVEN SNAPSHOTS REPO URL]
    publisher.experimentalRepository.username = [YOUR USERNAME]
    publisher.experimentalRepository.password = [YOUR PASSWORD]
    
    
You just need the groupId, version and artifactId configuration:
    
    publisher.groupId = [YOUR GROUPID FOR MAVEN]
    publisher.artifactId = project.name // Or whatever...
    publisher.version = [YOUR LIBRARY VERSION]
    
    
**What if my project.name is not the same as my artifactId ?**
To be able to integrate with bintray, we use the bintray gradle plugin from jfrog (added in the classpath).
The plugin uses the project.name to create the file path in bintray and so, if the project.name is different from
the artifactId, the path is not correctly created.

To solve this, the project.name must be changed in the settings.gradle file so that the bintray uploader correctly
creates the path.

For example, suppose you have the following structure:

    rootProject
      |- myLibraryProject
         |- src
         |- build.gradle
      |- mySampleApp
         |- src
         |- build.gradle
      |- build.gradle
      |- settings.gradle
      
Suppose we want to publish _myLibraryProject_ as _myCoolArtifact_. Then, the settings.gradle file should have:
    
    project(':myLibraryProject').name = 'myCoolArtifact'
    
Bare in mind that changing the _project.name_ value changes the name used when running gradle tasks or when directly 
including the project for tests. 
As an example, the _mySampleApp_ could have a reference to _myLibraryProject_ in the following way:

    compile project(':myCoolArtifact')

**Notice that the new name should be used
    
The gradle tasks should be run using the new name also.

    ./gradlew :myCoolArtifact:build :myCoolArtifact:test
     
     
## Further help
If you need further help, please contact [martin.heras@mercadolibre.com](mailto:martin.heras@mercadolibre.com) or [mobile-it@mercadolibre.com](mailto:mobile-it@mercadolibre.com).


