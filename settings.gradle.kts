plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "CraftMaps"

include("commonWeb")
include("common")
include("platformPaper")
