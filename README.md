Neurix
A modern, premium AI companion Android application built with Kotlin, Jetpack Compose, and Material 3.

Overview
Neurix is an AI-powered assistant application designed with a focus on beautiful UI, smooth animations, and scalable architecture. This initial bootstrap version establishes the perfect foundation for future development.

Technology Stack
Language: Kotlin
UI Framework: Jetpack Compose with Material 3
Architecture: Clean Architecture + MVI
DI: Hilt
Navigation: Navigation Compose
Async: Kotlin Coroutines & Flow
Build: Gradle Kotlin DSL with Version Catalog
Project Structure
neurix/
├── app/                    # Application module (entry point)
├── core/                   # Core aggregator module
├── core-common/            # Shared interfaces, base classes, MVI contracts
├── core-design/            # Theme, colors, typography, shapes, dimensions
├── core-ui/                # Reusable Compose components and placeholders
├── core-navigation/        # Navigation graph, routes, screen definitions
├── feature-home/           # Home screen with microphone button
├── feature-chat/           # Chat screen with message UI
├── feature-settings/       # Settings screen with placeholders
├── gradle/                 # Gradle wrapper and version catalog
├── build.gradle.kts        # Root build script
├── settings.gradle.kts     # Module inclusion and repository config
└── gradle.properties       # Gradle configuration
Features
Splash Screen: Minimal logo with fade animation
Home Screen: Greeting, subtitle, animated glowing microphone button
Chat Screen: Beautiful message bubbles with typing animation
Settings Screen: Profile card, themed settings rows with placeholder detail pages
Getting Started
Prerequisites
Android Studio Hedgehog or later
JDK 17
Android SDK 35
Build
./gradlew assembleDebug
Run
Open the project in Android Studio and run on a device or emulator with API 24+.

License
Proprietary — All rights reserved.