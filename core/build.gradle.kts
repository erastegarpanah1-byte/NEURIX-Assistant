plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neurix.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":core-common"))
    api(project(":core-design"))
    api(project(":core-ui"))
    api(project(":core-navigation"))
    api(project(":core-network"))
    api(project(":core-database"))
    api(project(":core-ai"))
    api(project(":core-speech"))
    api(project(":core-actions"))
    api(project(":core-service"))
}