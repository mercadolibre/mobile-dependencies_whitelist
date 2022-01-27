package com.mercadolibre.android.gradle.base.utils

/**
 * Utils for variant object
 */
final class VariantUtils {

    /**
     * get javaCompile from variant
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
     *  get packageLibrary from variant
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