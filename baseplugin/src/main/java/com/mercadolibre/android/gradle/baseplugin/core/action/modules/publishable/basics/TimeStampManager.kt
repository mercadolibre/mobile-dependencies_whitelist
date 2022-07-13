package com.mercadolibre.android.gradle.baseplugin.core.action.modules.publishable.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_FILE
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_GENERATOR
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISHING_TIME_ZONE
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.gradle.api.Project

object TimeStampManager {

    fun getOrCreateTimeStamp(project: Project): String {
        val file = project.rootProject.file(PUBLISHING_TIME_FILE)

        return if (file.exists()) {
            BufferedReader(InputStreamReader(file.inputStream(), StandardCharsets.UTF_8)).readLine()
        } else {
            file.parentFile.mkdirs()
            val time = SimpleDateFormat(PUBLISHING_TIME_GENERATOR)
                .apply { timeZone = TimeZone.getTimeZone(PUBLISHING_TIME_ZONE) }.format(Date())
            file.appendText(time)
            time
        }
    }

    fun deleteTimeStampFile(project: Project) {
        val file = project.rootProject.file(PUBLISHING_TIME_FILE)
        if (file.exists()) {
            file.delete()
        }
    }

}