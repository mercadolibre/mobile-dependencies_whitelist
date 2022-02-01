package com.mercadolibre.android.gradle.base.utils

/**
 * Utils for variant object
 */
final class VariantUtils {

    /**
     * Return java compile object from given variant
     * @param variant
     * @return javaCompile
     */
    static def javaCompile(def variant) {
        if (variant.hasProperty('javaCompileProvider')) {
            variant.javaCompileProvider.get()
        } else {
            variant.javaCompile
        }
    }

    /**
     * Return package library object from given variant
     * @param variant
     * @return packageLibrary
     */
    static def packageLibrary(def variant) {
        if (variant.hasProperty('packageLibraryProvider')) {
            return variant.packageLibraryProvider.get()
        } else {
            return variant.outputs.first().packageLibrary
        }
    }
}
