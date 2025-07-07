plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
}

tasks {
  runServer {
    minecraftVersion("1.21.5")
  }
}