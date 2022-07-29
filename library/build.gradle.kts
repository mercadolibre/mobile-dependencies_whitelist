plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation(libs.gradle.buildTools.plugin)
    implementation(libs.meli.gradle.plugin.baseplugin)

    testImplementation(libs.meli.gradle.plugin.baseplugin)
    testImplementation(libs.bundles.test)
}

gradlePlugin {
    plugins {
        register("mercadolibre.gradle.config.library") {
            id = "mercadolibre.gradle.config.library"
            implementationClass = "com.mercadolibre.android.gradle.library.BaseLibraryPlugin"
        }
    }
}
