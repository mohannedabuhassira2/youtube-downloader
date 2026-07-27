pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Youtube Downloader"
include(":app")
include(":navigation")
include(":core:designsystem")
include(":core:network")
include(":core:storage")
include(":features:downloader")
include(":features:history")
include(":testing:unit")
include(":testing:ui")
 