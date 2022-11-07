package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_ZONE
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * This class is in charge of generating a TimeStamp for all publication tasks, as long as they are the same.
 */
object TimeStampManager {

    private var time: String? = null

    /**
     * This method generates a time stamp.
     */
    fun createTimeStamp(format: String): String {
        return SimpleDateFormat(format).apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date())
    }

    /**
     * This method generates a time stamp the first time it is called and then returns the same object.
     */
    fun getOrCreateTimeStamp(pattern: String): String {
        if (time == null) {
            time = createTimeStamp(pattern)
        }
        return time!!
    }

    /**
     * This method removes the previously generated timestamp.
     */
    fun deleteTimeStamp() {
        time = null
    }
}
