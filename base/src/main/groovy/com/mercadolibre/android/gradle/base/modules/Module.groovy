package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

/**
 * Base interface of a module
 *
 * Created by saguilera on 7/21/17.
 */
interface Module {
    void configure(Project project)
}