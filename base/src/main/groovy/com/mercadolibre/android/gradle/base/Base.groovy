package com.mercadolibre.android.gradle.base

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle base plugin for MercadoLibre Android projects/modules.
 */
class BasePlugin implements Plugin<Project> {

    /**
     * The project.
     */
    private Project project;

    /**
     * Method called by Gradle when applying this plugin.
     * @param project the Gradle project.
     */
    void apply(Project project) {

        this.project = project

        avoidCacheForDynamicVersions()
        setupRepositories()
        linkSources()
    }

    /**
     * Avoids using Gradle caches for dynamic versions, so that we can use EXPERIMENTAL artifacts with the '+' wildcard.
     */
    private void avoidCacheForDynamicVersions() {
        // For all subprojects...
        project.gradle.allprojects {
            configurations.all {
                resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
            }
        }
    }

    /**
     * Sets up the repositories.
     */
    private void setupRepositories() {
        // For all subprojects...
        project.gradle.allprojects {
            repositories {
                jcenter()
                mavenLocal()
                mavenCentral()
                maven {
                    url 'http://maven-mobile.melicloud.com/nexus/content/repositories/releases'
                }
                maven {
                    url 'http://maven-mobile.melicloud.com/nexus/content/repositories/experimental'
                }
            }
        }
    }

    /**
     * Link the sources JAR with the library itself, by modifying the .idea/library XMLs.
     */
    private void linkSources() {

        // Get the directory in which AS / Idea manages the references to external libraries...
        File librariesDir = project.file('./.idea/libraries')

        // Check that the directory exists...
        if (librariesDir.exists() && librariesDir.isDirectory()) {

            // For each XML inside the .idea/libraries directory...
            for (File libraryXml : librariesDir.listFiles()) {

                // Get the root node of the XML.
                Node root = new XmlParser().parse(libraryXml)

                // Check that there are no existing sources already set...
                def existingSourcesRoots = root.library[0].SOURCES[0].root
                if (existingSourcesRoots.isEmpty()) {

                    // For each root node under CLASSES node...
                    for (def classesRoot : root.library[0].CLASSES[0].root) {

                        // Get the 'url' attribute.
                        String url = classesRoot.@url

                        // Filter those who start with 'jar://', as there are some that points to
                        // resources using 'file://'. We don't want that.
                        if (url.startsWith('jar://')) {

                            // If the url contains 'exploded-aar' and also contains 'classes.jar',
                            // the library is an AAR, pointing to the 'exploded-aar' directory,
                            // that is managed by AS / Idea. Otherwise, it is a JAR.
                            if (url.contains('/exploded-aar/')) {
                                if (url.contains('classes.jar!')) {
                                    String[] urlComponents = url.split('/')
                                    int urlComponentsCount = urlComponents.size()
                                    String version = urlComponents[urlComponentsCount - 2]
                                    String artifactId = urlComponents[urlComponentsCount - 3]
                                    String groupId = urlComponents[urlComponentsCount - 4]
                                    addSources(libraryXml, root, groupId, artifactId, version)
                                }
                            } else {
                                String[] urlComponents = url.split('/')
                                int urlComponentsCount = urlComponents.size()
                                String version = urlComponents[urlComponentsCount - 3]
                                String artifactId = urlComponents[urlComponentsCount - 4]
                                String groupId = urlComponents[urlComponentsCount - 5]
                                addSources(libraryXml, root, groupId, artifactId, version)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds the sources JAR to the root's SOURCES child, if it does not exist already.
     * @param libraryXml the file pointing to the XML of the library.
     * @param root the root node.
     * @param groupId the groupId of the library.
     * @param artifactId the artifactId of the library.
     * @param version the version of the library.
     */
    private void addSources(File libraryXml, Node root, String groupId, String artifactId, String version) {

        // Get the path of the artifact cache directory.
        File gradleCacheDir = project.file(project.gradle.getGradleUserHomeDir().absolutePath + '/caches/modules-2/files-2.1/' + groupId + '/' + artifactId + '/' + version)
        if (gradleCacheDir.exists() && gradleCacheDir.isDirectory()) {

            // Get the hash directories and sort them by the 'last modified' attribute.
            File[] hashDirs = gradleCacheDir.listFiles()
            Arrays.sort(hashDirs, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified())
                }
            });

            // Get the first hash directory that contains a JAR with sources,
            // and use that to make the library point to its sources.
            for (File hashDir : hashDirs) {
                if (hashDir.isDirectory()) {
                    File[] files = hashDir.listFiles()
                    if (files.size() > 0 && files[0].absolutePath.contains(artifactId + '-' + version + '-sources.jar')) {
                        root.library[0].SOURCES[0].appendNode('root', [url: 'jar://' + files[0].absolutePath + '!/'])
                        new XmlNodePrinter(new PrintWriter(new FileWriter(libraryXml))).print(root)
                        break;
                    }
                }
            }
        }
    }
}