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
        versionCode = 2
        versionName = "1.1.0"
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
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("org.jetbrains.compose.runtime:runtime:1.8.1")
    implementation("org.jetbrains.compose.foundation:foundation:1.8.1")
    implementation("org.jetbrains.compose.ui:ui:1.8.1")
    implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.8.1")
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.8.1")

    implementation("androidx.compose.material3:material3:1.4.0-alpha14")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0-alpha14")

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
}
