pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    create(rootProject) {
        version("26.2-fabric", "26.2").buildscript("build.fabric.26.gradle.kts")
        vcsVersion = "26.2-fabric"
    }
}

rootProject.name = "Waylight"
