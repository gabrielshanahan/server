plugins {
    id("application-conventions")
    alias(libs.plugins.gatling)
}

dependencies {
    implementation(project(":library"))
    implementation(libs.kotlin.coroutines)

    implementation(libs.bundles.gatling)

    testImplementation(libs.okHttp)
}

application {
    mainClass = "io.github.gabrielshanahan.server.ServerKt"
}

gatling {
    gatlingVersion = libs.versions.gatling.plugin.get()
}
