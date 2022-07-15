package com.mercadolibre.android.gradle.baseplugin.core.action.providers

import com.android.build.gradle.api.BaseVariant
import com.mercadolibre.android.gradle.baseplugin.core.components.JAVA_COMPILE_PROVIDER
import com.mercadolibre.android.gradle.baseplugin.core.components.PACKAGE_LIBRARY_PROVIDER
import groovy.lang.MetaProperty
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.tasks.compile.JavaCompile

/**
 * VariantUtils is in charge of providing functionalities to explore the particularities of a variant.
 */
object VariantUtils {

    /**
     * This method is in charge of returning an available JavaCompile of a variant.
     */
    fun javaCompile(variant: BaseVariant): JavaCompile =
        if (hasProperty(variant, JAVA_COMPILE_PROVIDER) != null) {
            variant.javaCompileProvider.get()
        } else {
            variant.javaCompile
        }

    /**
     * This method is in charge of returning the library package of a variant.
     */
    fun packageLibrary(variant: BaseVariant): Any? =
        if (hasProperty(variant, PACKAGE_LIBRARY_PROVIDER) != null) {
            getProperty(variant, PACKAGE_LIBRARY_PROVIDER)
        } else {
            variant.outputs.first().dirName
        }

    private fun hasProperty(self: BaseVariant, name: String): MetaProperty? = InvokerHelper.getMetaClass(self).hasProperty(self, name)

    private fun getProperty(self: BaseVariant, name: String): Any? = InvokerHelper.getProperty(self, name)
}
