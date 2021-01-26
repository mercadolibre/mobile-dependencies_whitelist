package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.JavaPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration

class PublishJarPublicReleaseTask extends PublishJarReleaseTask {

    @Override
    BintrayConfiguration.Builder getBintrayConfiguration() {
        return new BintrayConfiguration.Builder().with {
            project = this.project
            bintrayRepository = BINTRAY_PUBLIC_REPOSITORY
            publicationName = this.taskName
            publicationPackaging = JavaPublishableModule.PACKAGING
            publicationType = 'Public'
            return it
        }
    }
}
