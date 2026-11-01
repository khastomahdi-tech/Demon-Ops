plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.demon.ops"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.demon.ops"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("dg") {
            storeFile = file("dg.keystore")
            storePassword = "dgDemon2026"
            keyAlias = "dg"
            keyPassword = "dgDemon2026"
        }
    }

    buildTypes {
        debug { signingConfig = signingConfigs.getByName("dg") }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("dg")
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
}
