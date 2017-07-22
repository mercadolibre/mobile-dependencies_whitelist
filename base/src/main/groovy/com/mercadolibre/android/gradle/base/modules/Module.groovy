package com.mercadolibre.android.gradle.base.modules

import org.gradle.api.Project

import java.text.SimpleDateFormat

/**
 * Created by saguilera on 7/21/17.
 */
abstract class Module {

    protected static final String JACOCO_PLUGIN_CLASSPATH = "com.mercadolibre.android.gradle/jacoco"
    protected static final String ROBOLECTRIC_PLUGIN_CLASSPATH = "com.mercadolibre.android.gradle/robolectric"

    abstract void configure(Project project)

    protected String getTimestamp() {
        def sdf = new SimpleDateFormat('yyyyMMddHHmmss')
        sdf.timeZone = TimeZone.getTimeZone('UTC')
        sdf.format(new Date())
    }

}