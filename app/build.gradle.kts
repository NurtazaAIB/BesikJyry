plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "kz.nurtaza.besikjyry"
    compileSdk = 35
    defaultConfig {
        applicationId = "kz.nurtaza.besikjyry"
        minSdk = 23
        targetSdk = 33
        versionCode = (System.getenv("CI_BUILD_NUMBER") ?: "1").toInt()
        versionName = "1.0." + versionCode
    }
    signingConfigs {
        create("published") {
            val keyPath = System.getenv("RELEASE_KEYSTORE_PATH")
            if (!keyPath.isNullOrBlank()) {
                storeFile = file(keyPath)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("published")
        }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
