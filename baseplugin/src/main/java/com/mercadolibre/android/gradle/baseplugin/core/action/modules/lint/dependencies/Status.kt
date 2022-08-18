package com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies

import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_AVAILABLE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_EXPIRED
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_GOING_TO_EXPIRE
import com.mercadolibre.android.gradle.baseplugin.core.components.LINT_INVALID

/**
 * The Status class is responsible for providing all the possibilities of dependencies within the allowlist.
 */
object Status {

    /**
     * This method is responsible for generating an available dependency state.
     */
    fun available(): StatusBase = StatusBase(shouldReport = false, isBlocker = false, LINT_AVAILABLE, null)

    /**
     * This method is responsible for generating an invalid dependency state.
     */
    fun invalid(message: String?): StatusBase = StatusBase(shouldReport = true, isBlocker = true, LINT_INVALID, message)

    /**
     * This method is responsible for generating an expired dependency state.
     */
    fun expired(message: String?): StatusBase = StatusBase(shouldReport = true, isBlocker = true, LINT_EXPIRED, message)

    /**
     * This method is responsible for generating an goign to expire dependency state.
     */
    fun goingToExpire(message: String?): StatusBase = StatusBase(shouldReport = true, isBlocker = false, LINT_GOING_TO_EXPIRE, message)
}
