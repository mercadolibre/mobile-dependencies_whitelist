plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    compileOnly(libs.meli.gradle.plugin.app)
    compileOnly(libs.meli.gradle.plugin.library)

    testImplementation(libs.meli.gradle.plugin.app)
    testImplementation(libs.meli.gradle.plugin.library)

    testImplementation(libs.bundles.test)

    implementation(libs.gradle.scan.enterprise)
    compileOnly(libs.misc.dexcount)
    implementation(libs.gradle.buildTools.plugin)
}

gradlePlugin {
    plugins {
        register("mercadolibre.gradle.config.settings") {
            id = "mercadolibre.gradle.config.settings"
            implementationClass = "com.mercadolibre.android.gradle.baseplugin.BasePlugin"
        }
    }
}
