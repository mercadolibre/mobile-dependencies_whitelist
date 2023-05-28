package com.mercadolibre.android.gradle.baseplugin.core.extensions

import java.io.File

fun File.new(): File {
    if (exists()) {
        delete()
    } else {
        parentFile.mkdirs()
    }
    return this
}
