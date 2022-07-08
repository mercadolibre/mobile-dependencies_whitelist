package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.basics

import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_AVAILABLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_EXPIRED
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_GOING_TO_EXPIRE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_INVALID

/**
 * The Status class is responsible for providing all the possibilities of dependencies within the allowlist.
 */
class Status {

    /**
     * This method is responsible for generating an available dependency state.
     */
    fun available(): StatusBase {
        return StatusBase(shouldReport = false, isBlocker = false, LINT_AVAILABLE)
    }

    /**
     * This method is responsible for generating an invalid dependency state.
     */
    fun invalid(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true, LINT_INVALID)
    }

    /**
     * This method is responsible for generating an expired dependency state.
     */
    fun expired(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = true, LINT_EXPIRED)
    }

    /**
     * This method is responsible for generating an goign to expire dependency state.
     */
    fun goignToExpire(): StatusBase {
        return StatusBase(shouldReport = true, isBlocker = false, LINT_GOING_TO_EXPIRE)
    }
}
