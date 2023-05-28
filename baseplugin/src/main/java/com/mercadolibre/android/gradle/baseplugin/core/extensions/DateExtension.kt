package com.mercadolibre.android.gradle.baseplugin.core.extensions

import java.text.SimpleDateFormat

private const val DATE_PATTERN = "yyyy-MM-dd"

internal fun String.asMilliseconds() = SimpleDateFormat(DATE_PATTERN)
    .parse(replace("\\", ""))
    .time