plugins {
    id("application-conventions")
}

dependencies {
    implementation(project(":library"))
}

application {
    mainClass = "io.github.gabrielshanahan.server.AppKt"
}
