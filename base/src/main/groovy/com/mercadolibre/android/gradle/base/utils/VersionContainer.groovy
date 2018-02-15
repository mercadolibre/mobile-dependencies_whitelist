package com.mercadolibre.android.gradle.base.utils

/**
 * Created by saguilera on 7/24/17.
 */
final class VersionContainer {

    private static Map<String, String> map

    public static void init() {
        map = new HashMap<>()
    }

    private static String key(String projectName, String publicationName) {
        return "${projectName}-${publicationName}"
    }

    public static void put(String projectName, String publicationName, String version) {
        if (!map.get(key(projectName, publicationName))) {
            map.put(key(projectName, publicationName), version)
        }
    }

    public static String get(String projectName, String publicationName, String defaultValue = '') {
        return map.get(key(projectName, publicationName), defaultValue)
    }

    public static void logVersion(String version) {
        println ("${(27 as Character)}[32mPublishing version: ${version} ${(27 as Character)}[0m")
    }

}
