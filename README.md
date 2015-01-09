MercadoLibre Gradle Android Plugins for Gradle
==============================

## What is this?
This project includes three Gradle plugins to make the Android library developer's life easier:

1. aar-publisher
2. jar-publisher
3. base

## What do the aar-publisher / jar-publisher plugins do for us?

These plugins add new tasks to the Gradle build script that applies them (except the base plugin). The goal of these tasks is to publish Android libraries to Maven repositories, but running some other important tasks before:

1. Generate JAR containing source code per each Android variant.
2. Generate HTMLs with all the related Javadoc per each Android variant.
3. Generate JAR containing the Javadoc's HTMLs.
4. Create lint reports (only if publishing as release - see below).
5. Run Android (Instrumentation) tests (only for aar-publisher).
6. Run JUnit tests (only for jar-publisher).
7. Create Jacoco reports (only for jar-publisher).
8. Upload the artifacts (library .aar/.jar, sources and Javadoc) to the Maven repository, depending on what task you run (see below).
9. Tag the new version in Git (only if publishing as release - see below).

In order to get all of this done, the plugins add new tasks to your build script, so that you can call them with the Gradle command line tool.

## Tasks added by aar-publisher

1. `publishAarRelease` - Publishes the .aar (along with the sources and Javadoc) to the specified Maven releases repository. It runs all the checks before uploading the artifacts (lint, tests, etc.), and once they get uploaded, it tags the version in Git.
2. `publishAarExperimental` - Same as `publishAarRelease`, but it uploads the artifacts to the specified Maven experimental repository. It does NOT tag the version in Git, as it is not a release.
3. `publishAarLocal` - This is particularly useful during the development phase of the Android library. It overwrites the artifacts in your .m2/repository directory, so that you can code & test your code without uploading anything to a remote repository. This does NOT run lint so that we don't have to wait that long. It does not tag the version in Git either.

## How to add aar-publisher to your project?

Example: [mobile-android_commons](https://github.com/mercadolibre/mobile-android_commons)

Simple. You just need to apply the plugin and configure it in the build script, as the following snippet shows:

**Parent build.gradle**
```groovy
apply plugin: 'idea'
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
        classpath 'com.mercadolibre.android.gradle:base:1.1'
        classpath 'com.mercadolibre.android.gradle.publisher:aar-publisher:1.1'
    }
}

idea {
    module {
        downloadJavadoc = true
        downloadSources = true
	}
}
```
**Your module's build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.publisher.aar'

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

## Tasks added by jar-publisher

1. `publishJarRelease` - Publishes the .jar (along with the sources and Javadoc) to the specified Maven releases repository. It runs all the checks before uploading the artifacts (tests, Jacoco, etc.), and once they get uploaded, it tags the version in Git.
2. `publishJarExperimental` - Same as `publishJarRelease`, but it uploads the artifacts to the specified Maven experimental repository. It does NOT tag the version in Git, as it is not a release.
3. `publishJarLocal` - This is particularly useful during the development phase of the Java library. It overwrites the artifacts in your .m2/repository directory, so that you can code & test your code without uploading anything to a remote repository. It does not tag the version in Git.

## How to add jar-publisher to your project?

Example: [mobile-android_model](https://github.com/mercadolibre/mobile-android_model)

Simple. You just need to apply the plugin and configure it in the build script, as the following snippet shows:

**Parent build.gradle**
```groovy
apply plugin: 'idea'
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
        classpath 'com.mercadolibre.android.gradle:base:1.1'
        classpath 'com.mercadolibre.android.gradle.publisher:jar-publisher:1.1'
    }
}

idea {
    module {
        downloadJavadoc = true
        downloadSources = true
    }
}
```    
**Your module's build.gradle**
```groovy
apply plugin: 'com.mercadolibre.android.gradle.publisher.jar'

publisher.releasesRepository.url = [YOUR MAVEN RELEASES REPO URL]
publisher.releasesRepository.username = [YOUR USERNAME]
publisher.releasesRepository.password = [YOUR PASSWORD]

publisher.snapshotsRepository.url = [YOUR MAVEN SNAPSHOTS REPO URL]
publisher.snapshotsRepository.username = [YOUR USERNAME]
publisher.snapshotsRepository.password = [YOUR PASSWORD]

publisher.groupId = [YOUR GROUPID FOR MAVEN]
publisher.artifactId = project.name // Or whatever...
publisher.version = [YOUR LIBRARY VERSION]

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
```    
As you can see, there is no need to apply the _java_ nor the _maven_ plugins, as they are automatically applied by the jar-publisher plugin.

## What does the base plugin do for us?

This plugin helps us on configuring the custom Nexus repositories when using our custom Android Libraries as dependencies in Gradle.

All you have to do is:

**Parent build.gradle**
```groovy
apply plugin: 'idea'
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
        classpath 'com.mercadolibre.android.gradle:base:1.1' // Let Gradle know that we are going to need this plugin.
    }
}

idea {
    module {
        downloadJavadoc = true
        downloadSources = true
    }
}
```   

**Your module's build.gradle**
```groovy
dependencies {
	// The following dependencies are just examples! Notice that we are using a wildcard to always get the latest EXPERIMENTAL artifact from particular versions. The Base plugin handles the Gradle cache for us, so that this module is always pointing to the latest EXPERIMENTAL dependencies.
    compile ('com.mercadolibre.android:networking:0.0.1-EXPERIMENTAL-+')
    compile ('com.mercadolibre.android:commons:0.1.0-EXPERIMENTAL-+@aar')
}
```   

## How to improve or compile the plugins?
If you want to improve the Publisher plugins, you should follow these steps:

1. Clone the project.
2. Import the project from IntelliJ Community Edition (not Android Studio!) (NOTE: Import it, do not 'open' it). You MUST select the root build.gradle when importing the project, otherwise IntelliJ will not recognize all the Gradle modules. 
3. Make sure you have a JDK configured for the project: _File -> Project Structure -> Project SDK_.
4. Make the changes you need.
5. Publish a new version of the plugins to the plugins repository, with any of these methods:
    1. Locally (on your .m2/repository directory): `./gradlew aar-publisher:install` or `./gradlew jar-publisher:install`
    2. Remotelly (on Nexus): `./gradlew aar-publisher:uploadArchives` or `./gradlew jar-publisher:uploadArchives` - You can check if the plugin has been uploaded by browsing [Nexus](http://maven-mobile.melicloud.com/nexus/content/repositories/). If you want to publish as a snapshot, make sure the version ends with "-SNAPSHOT", otherwise it will get uploaded as a release. See the inner build.gradle to modify the version.

## Possible errors with jar-publisher and aar-publisher

- If you get a 400 HTTP error while uploading the artifacts, make sure you are not trying to publish an existing release version. For instance, Nexus repository refuses to save an artifact in the release directory if the version already exists.

## Changelog

### jar-publisher

- 1.1: Removed `publishJarSnapshot`. Added `publishJarExperimental`.
- 1.0: First version of the plugin!

### aar-publisher

- 1.1: Removed `publishAarSnapshot`. Added `publishAarExperimental`.
- 1.0: First version of the plugin!

### base

- 1.2: Added mavenLocal() as default repository.
- 1.1: Added mavenCentral() as default repository.
- 1.0: First version of the plugin!

## Further help
If you need further help, please contact [martin.heras@mercadolibre.com](mailto:martin.heras@mercadolibre.com).
