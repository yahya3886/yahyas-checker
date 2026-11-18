plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "org.yahya3886.rootchecker"
    compileSdk = 35
    buildToolsVersion = "34.0.4"

    defaultConfig {
        applicationId = "org.yahya3886.rootchecker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("top.yukonga.miuix.kmp:miuix-android:0.4.7")
    implementation("org.jetbrains.compose.runtime:runtime:1.8.1")
    implementation("org.jetbrains.compose.foundation:foundation:1.8.1")
    implementation("org.jetbrains.compose.ui:ui:1.8.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
}
