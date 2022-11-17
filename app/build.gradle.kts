plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation(libs.gradle.buildTools.plugin)
    implementation(libs.meli.gradle.plugin.baseplugin)

    testImplementation(libs.bundles.test)

    testImplementation(libs.meli.gradle.plugin.baseplugin)

    implementation(libs.misc.bugsnag.plugin)
    testImplementation(libs.misc.bugsnag.plugin)

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
