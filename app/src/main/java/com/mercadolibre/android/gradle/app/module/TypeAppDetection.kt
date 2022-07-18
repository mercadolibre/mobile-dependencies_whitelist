package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

/**
 * This class is responsible for generating a complex extension that receives information on whether the module is a productive app or not.
 */
open class TypeAppDetection constructor(objects: ObjectFactory) {

    val appDetection: AppDetection = objects.newInstance(AppDetection::class.java)

    /**
     * This method is responsible for generating a new instance of the AppDetection class.
     */
    fun appDetection(action: Action<AppDetection>) {
        action.execute(appDetection)
    }

    companion object {
        /**
         * This method is responsible for generating the complex extension in the project.
         */
        fun Project.appDetection(): TypeAppDetection {
            return extensions.create(MELI_GROUP, TypeAppDetection::class.java)
        }
    }

    open class AppDetection {
        var isProductiveApp = false

        /**
         * This method is responsible for setting the variable that contains the information on whether it is a productive app or not.
         */
        fun isProductiveApp(boolean: Boolean) {
            isProductiveApp = boolean
        }
    }
}
