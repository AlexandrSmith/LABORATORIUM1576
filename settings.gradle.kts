rootProject.name = "school1576"

include("backend")

buildCache {
    local {
        // directory = ".gradle-build-cache"
        removeUnusedEntriesAfterDays = 1
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
//        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    val kotlinVersion: String by settings
    val shadowVersion: String by settings
    val ktlintVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        id("com.github.johnrengelman.shadow") version shadowVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }
}
