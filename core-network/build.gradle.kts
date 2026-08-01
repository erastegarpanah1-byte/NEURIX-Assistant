plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.neurix.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        buildConfigField("String", "OR_A", "\"sk-or-v1-aabb6642188018f7c3799f332fe56fd\"")
        buildConfigField("String", "OR_B", "\"9d65c0fc029096443f8d48677ed1a04a1\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core-common"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
