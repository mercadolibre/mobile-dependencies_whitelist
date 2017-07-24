package com.mercadolibre.android.gradle.base.publish

import org.gradle.api.Project
import org.gradle.api.Task

import java.text.SimpleDateFormat

/**
 * Created by saguilera on 7/21/17.
 */
abstract class PublishTask {

    protected static final String BINTRAY_RELEASE_REPOSITORY = 'android-releases'
    protected static final String BINTRAY_EXPERIMENTAL_REPOSITORY = 'android-experimental'

    abstract Task create(Builder builder)

    protected String getTimestamp() {
        def sdf = new SimpleDateFormat('yyyyMMddHHmmss')
        sdf.timeZone = TimeZone.getTimeZone('UTC')
        return sdf.format(new Date())
    }

    static class Builder {

        Project project = null

        def variant = null

        String taskName = null

    }

}
