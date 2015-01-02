MercadoLibre Android Library Publisher Plugins for Gradle
==============================

## What is this?
This project includes two Gradle plugins to make the Android library developer's life easier:

1. aar-publisher
2. jar-publisher

These plugins add new tasks to the Gradle build script that applies them. The goal of these tasks is to publish Android libraries to Maven repositories, but running some other important tasks before:

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

1. `./gradlew publishAarRelease` - Publishes the .aar (along with the sources and Javadoc) to the specified Maven releases repository. It runs all the checks before uploading the artifacts (lint, tests, etc.), and once they get uploaded, it tags the version in Git.
2. `./gradlew publishAarSnapshot` - Same as `publishAarRelease`, but it uploads the artifacts to the specified Maven snapshots repository. It does NOT tag the version in Git, as it is not a release.
3. `./gradlew publishAarLocal` - This is particularly useful during the development phase of the Android library. It overwrites the artifacts in your .m2/repository directory, so that you can code & test your code without uploading anything to a remote repository. This does NOT run lint so that we don't have to wait that long. It does not tag the version in Git either.

## How to add it to your project?

Simple. You just need to apply the plugin and configure it in the build script, as the following snippet shows:

### Parent build.gradle

    apply plugin: 'idea'

    buildscript {
    	repositories {
        	jcenter()
        	mavenLocal()
    	}
    	dependencies {
        	classpath 'com.android.tools.build:gradle:1.0.0'
        	classpath 'com.mercadolibre.android.gradle.publisher:aar-publisher:0.1-SNAPSHOT'
        	classpath 'com.mercadolibre.android.gradle.publisher:jar-publisher:0.1-SNAPSHOT'
    	}
 	}
    
	allprojects {
    	repositories {
        	jcenter()
        	maven {
            	url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases'
            }
        	maven {
            	url 'http://maven-mobile.melicloud.com/nexus/content/repositories/snapshots'
            }
    	}
	}

	idea {
    	module {
        	downloadJavadoc = true
        	downloadSources = true
    	}
	}

### Your module's build.gradle





## How to improve it?
If you want to improve the Publisher plugins, you should follow these steps:
1. Clone the project.
2. Import the project from Android Studio (NOTE: Import it, do not 'open' it).
3. On the left panel, select the 'Project' view, instead of the 'Android' view, as this project is made with Groovy and the 'Android' view does not work well with .groovy source sets.
4. Make the changes you need.
5. Publish a new version of the plugins to the plugins repository (**WIP**).

## Further help
If you need further help, please contact [martin.heras@mercadolibre.com](mailto:martin.heras@mercadolibre.com).
