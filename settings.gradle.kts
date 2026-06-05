pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)  // ← ИЗМЕНИЛ с FAIL_ON_PROJECT_REPOS на PREFER_PROJECT
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()  // ← ДОБАВИЛ
    }
}

rootProject.name = "Homework4"  // ← УБРАЛ пробел (было "Homework 4")
include(":app")