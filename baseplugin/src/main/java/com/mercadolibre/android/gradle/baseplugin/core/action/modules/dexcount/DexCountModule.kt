package com.mercadolibre.android.gradle.baseplugin.core.action.modules.dexcount

import com.getkeepsafe.dexcount.DexCountExtension
import com.mercadolibre.android.gradle.baseplugin.core.basics.ExtensionGetter
import com.mercadolibre.android.gradle.baseplugin.core.components.DEXCOUNT_PLUGIN
import com.mercadolibre.android.gradle.baseplugin.core.components.DEXCOUNT_PROPERTY
import com.mercadolibre.android.gradle.baseplugin.core.components.JSON_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class DexCountModule: Module, ExtensionGetter() {
    override fun configure(project: Project) {
        if (project.hasProperty(DEXCOUNT_PROPERTY)) {
            project.apply(plugin = DEXCOUNT_PLUGIN)

            findExtension<DexCountExtension>(project)?.apply {
                // more config options: https://github.com/KeepSafe/dexcount-gradle-plugin#configuration
                format = JSON_CONSTANT
                includeClassCount = true
                includeFieldCount = true
                includeTotalMethodCount = true
            }
        }
    }
}