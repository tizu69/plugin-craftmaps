plugins {
}

repositories {
}

dependencies {
    implementation("io.javalin:javalin:6.7.0")
}

tasks {
    "jar" {
        dependsOn(":commonWeb:pnpmBuild")
    }
}
