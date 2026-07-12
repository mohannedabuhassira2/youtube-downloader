plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mohannedabuhassira.youtubedownloader.testing.unit"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.core.ktx)
}
