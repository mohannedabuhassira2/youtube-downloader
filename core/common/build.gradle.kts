plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mohannedabuhassira.youtubedownloader.core.common"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
}
