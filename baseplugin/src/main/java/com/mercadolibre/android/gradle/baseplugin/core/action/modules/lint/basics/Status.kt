package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

internal class Status {

    fun available(): StatusBase {
        return StatusBase(shouldReport = false, isBlocker = false)
    }

    fun invalid(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true)
    }

    fun expired(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true)
    }

    fun goign_to_expire(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = false)
    }

}

