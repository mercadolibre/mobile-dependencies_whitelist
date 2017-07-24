package com.mercadolibre.android.gradle.base.utils

/**
 * Created by saguilera on 7/24/17.
 */
final class VersionContainer {

    private static final Map<String, String> map = new HashMap<>()

    public static void put(String publicationName, String version) {
        map.put(publicationName, version)
    }

    public static String get(String publicationName, String defaultValue = '') {
        return map.get(publicationName, defaultValue)
    }

    public static void logVersion(String version) {
        println ("${(27 as Character)}[32mPublishing version: ${version} ${(27 as Character)}[0m")
    }

}
