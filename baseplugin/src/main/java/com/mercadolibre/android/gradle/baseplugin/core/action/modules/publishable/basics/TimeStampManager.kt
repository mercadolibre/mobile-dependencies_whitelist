package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_GENERATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_ZONE
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object TimeStampManager {

    private var time: String? = null

    fun getOrCreateTimeStamp(): String {
        if (time == null) {
            time = SimpleDateFormat(PUBLISHING_TIME_GENERATOR).apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date())
        }
        return time!!
    }

    fun deleteTimeStamp() {
        time = null
    }
}
