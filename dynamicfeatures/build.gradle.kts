plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation(libs.gradle.buildTools.plugin)

    testImplementation(libs.bundles.test)

    implementation(libs.meli.gradle.plugin.baseplugin)
    testImplementation(libs.meli.gradle.plugin.baseplugin)
}

gradlePlugin {
    plugins {
        register("mercadolibre.gradle.config.dynamicfeatures") {
            id = "mercadolibre.gradle.config.dynamicfeatures"
            implementationClass = "com.mercadolibre.android.gradle.dynamicfeature.DynamicFeaturesPlugin"
        }
    }
}
