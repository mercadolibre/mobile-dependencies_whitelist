package com.mercadolibre.android.gradle.base.publish

import com.mercadolibre.android.gradle.base.modules.AndroidLibraryPublishableModule
import com.mercadolibre.android.gradle.base.utils.BintrayConfiguration

class PublishAarPrivateReleaseTask extends PublishAarReleaseTask {

    @Override
    BintrayConfiguration.Builder getBintrayConfiguration() {
        return new BintrayConfiguration.Builder().with {
            project = this.project
            bintrayRepository = BINTRAY_RELEASE_REPOSITORY
            publicationName = this.taskName
            publicationPackaging = AndroidLibraryPublishableModule.PACKAGING
            publicationType = 'Release'
            return it
        }
    }
}
