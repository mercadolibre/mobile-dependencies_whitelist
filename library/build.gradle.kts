plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    
    implementation(libs.gradle.buildTools.plugin)

    testImplementation(libs.meli.gradle.plugin.app)
    testImplementation(libs.meli.gradle.plugin.baseplugin)

    testImplementation(libs.bundles.test)

    implementation(libs.meli.gradle.plugin.baseplugin)
}

gradlePlugin {
    plugins {
        register("mercadolibre.gradle.config.library") {
            id = "mercadolibre.gradle.config.library"
            implementationClass = "com.mercadolibre.android.gradle.library.BaseLibraryPlugin"
        }
    }
}