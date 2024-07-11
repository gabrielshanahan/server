import org.gradle.accessors.dm.LibrariesForLibs

// Necessary to allow using version catalog in precompiled script plugins.
// See https://github.com/gradle/gradle/issues/15383 for more info.
val libs = the<LibrariesForLibs>()

plugins {
    // We cannot use the type safe API generated from the version catalog here,
    // because reasons. I think it has something to do with the fact that this
    // part is actually executed *before* the typesafe accessors are generated,
    // because it is needed in order to determine which accessors should be
    // generated in the first place.
    // For more info, read through  https://github.com/gradle/gradle/issues/15383
    //
    // However, the important thing is that we can still manage the plugin *versions*
    // via the version catalog (by declaring it in the dependency { } block of this
    // projects build.gradle.kts)
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("com.ncorti.ktfmt.gradle")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.logback)

    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
}

java {
    /**
     * This will cause the correct JVM to download if not available.
     * The repository is defined via the foojay-resolver plugin.
     */
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    toolVersion = libs.versions.detekt.plugin.get()
    config.setFrom(file("${rootProject.projectDir}/config/detekt/detekt.yml"))
    baseline = file("config/detekt/baseline.xml")
    buildUponDefaultConfig = true
}

ktfmt {
    kotlinLangStyle()
}
