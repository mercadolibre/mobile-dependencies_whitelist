plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation(libs.gradle.buildTools.plugin)

    testImplementation(libs.meli.gradle.plugin.library)
    testImplementation(libs.meli.gradle.plugin.baseplugin)

    testImplementation(libs.bundles.test)

    implementation(libs.meli.gradle.plugin.baseplugin)
}

gradlePlugin {
    plugins {
        register("mercadolibre.gradle.config.app") {
            id = "mercadolibre.gradle.config.app"
            implementationClass = "com.mercadolibre.android.gradle.app.BaseAppPlugin"
        }
    }
}