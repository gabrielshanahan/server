
plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build
    // scripts in 'src/main' that automatically become available as plugins in the
    // main build.
    // For more info, see e.g. https://medium.com/@yudistirosaputro/gradle-convention-plugins-a-powerful-tool-for-reusing-build-configuration-ba2b250d9063
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // Necessary to allow using version catalog in convention plugins.
    // See https://github.com/gradle/gradle/issues/15383 for more info.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // Version of external plugins used in convention plugins must
    // be managed inside the dependencies { } block.
    // See https://docs.gradle.org/current/userguide/custom_plugins.html#sec:convention_plugins
    // and https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html#sec:applying_external_plugins
    // for more info
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.plugin)
    implementation(libs.ktfmt.plugin)
}
