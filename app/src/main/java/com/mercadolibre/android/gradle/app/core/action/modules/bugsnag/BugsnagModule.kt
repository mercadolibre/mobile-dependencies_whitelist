package com.mercadolibre.android.gradle.app.core.action.modules.bugsnag

import com.bugsnag.android.gradle.BugsnagPlugin
import com.bugsnag.android.gradle.BugsnagPluginExtension
import com.mercadolibre.android.gradle.baseplugin.core.components.BUGSNAG_EXTENSION
import com.mercadolibre.android.gradle.baseplugin.core.components.BUGSNAG_RETRY_CONVENTION
import com.mercadolibre.android.gradle.baseplugin.core.components.PUBLISH_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.ExtensionProvider
import com.mercadolibre.android.gradle.baseplugin.core.domain.interfaces.Module
import org.gradle.api.Project

/**
 * This class is responsible for adding bugsnag functionality to a repository.
 */
class BugsnagModule : Module(), ExtensionProvider {

    override fun getExtensionName(): String = BUGSNAG_EXTENSION

    /**
     * This method takes care of configuring Bugsnag within the project.
     */
    override fun configure(project: Project) {
        applyBugsnagPlugin(project)
        configureBugsnagExtension(project)
    }

    private fun applyBugsnagPlugin(project: Project) {
        project.plugins.apply(BugsnagPlugin::class.java)
    }

    private fun configureBugsnagExtension(project: Project) {
        findExtension<BugsnagPluginExtension>(project)?.apply {
            retryCount.convention(BUGSNAG_RETRY_CONVENTION)

            variantFilter {
                setEnabled(project.hasProperty(PUBLISH_CONSTANT))
            }
        }
    }
}
