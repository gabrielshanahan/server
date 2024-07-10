pluginManagement {
    /**
     * Extracting settings plugin management into a separate build
     * allows us to use the version catalog in that build. This is
     * the only benefit to doing things like this, as opposed to
     * directly specifying external plugins in the plugins { } block
     * bellow.
     */
    includeBuild("gradle/settings")
}

plugins {
    id("toolchains-resolver-convention")
}

includeBuild("gradle/platform")

rootProject.name = "server"
include("app", "library")
