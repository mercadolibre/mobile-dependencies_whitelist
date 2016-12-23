package com.mercadolibre.android.gradle.application

import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * Created by ngiagnoni on 3/11/15.
 */
public class ApplicationPlugin implements Plugin<Project> {

    def project

    @Override
    public void apply(Project project) {
        this.project = project

        project.apply plugin: 'com.android.application'

        project.apply plugin: 'com.mercadolibre.android.gradle.jacoco'
        project.apply plugin: 'com.mercadolibre.android.gradle.robolectric'

        project.apply plugin: 'nebula.dependency-lock'
    }

}
