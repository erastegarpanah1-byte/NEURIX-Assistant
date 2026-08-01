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

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Neurix"

include(":app")
include(":core")
include(":core-common")
include(":core-design")
include(":core-ui")
include(":core-navigation")
include(":core-network")
include(":core-database")
include(":core-ai")
include(":core-speech")
include(":core-actions")
include(":core-service")
include(":feature-home")
include(":feature-chat")
include(":feature-assistant")
include(":feature-settings")