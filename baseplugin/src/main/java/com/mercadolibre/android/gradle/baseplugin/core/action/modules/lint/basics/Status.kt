package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_AVAILABLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_EXPIRED
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_GOING_TO_EXPIRE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_INVALID

/**
 * The Status class is responsible for providing all the possibilities of dependencies within the allowlist.
 */
class Status {

    fun available(): StatusBase {
        return StatusBase(shouldReport = false, isBlocker = false, LINT_AVAILABLE)
    }

    fun invalid(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true, LINT_INVALID)
    }

    fun expired(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true, LINT_EXPIRED)
    }

    fun goign_to_expire(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = false, LINT_GOING_TO_EXPIRE)
    }
}
