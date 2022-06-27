package com.mercadolibre.android.gradle.app.module

import com.mercadolibre.android.gradle.baseplugin.core.components.MELI_GROUP
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

open class TypeAppDetection constructor(objects: ObjectFactory) {

    val appDetection: AppDetection = objects.newInstance(AppDetection::class.java)

    fun appDetection(action: Action<AppDetection>) {
        action.execute(appDetection)
    }

    companion object {
        fun Project.appDetection(): TypeAppDetection {
            return extensions.create(MELI_GROUP, TypeAppDetection::class.java)
        }
    }

    open class AppDetection {
        var isProductiveApp = false

        fun isProductiveApp(boolean: Boolean) {
            isProductiveApp = boolean
        }
    }
}
