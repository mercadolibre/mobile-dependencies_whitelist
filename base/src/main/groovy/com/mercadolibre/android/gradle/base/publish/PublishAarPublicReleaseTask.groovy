package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration

class PublishAarPublicReleaseTask extends PublishAarReleaseTask {
    @Override
    BintrayConfiguration.Builder getBintrayConfiguration() {
        return new BintrayConfiguration.Builder().with {
            project = this.project
            bintrayRepository = BINTRAY_PUBLIC_REPOSITORY
            publicationName = this.taskName
            publicationPackaging = AndroidLibraryPublishableModule.PACKAGING
            publicationType = 'Public'
            return it
        }
    }
}
