plugins {
    id("base")
    id("java")
    id("com.gradleup.shadow") version "8.3.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    if (project.name != "common") {
        apply(plugin = "com.gradleup.shadow")
        dependencies {
            implementation(project(":common"))
        }
    }
}
