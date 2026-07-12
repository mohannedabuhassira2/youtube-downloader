plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mohannedabuhassira.youtubedownloader.testing.ui"
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.junit)
}
