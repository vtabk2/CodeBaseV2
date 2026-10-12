plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.core.baseui"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:ads"))
    implementation(libs.androidx.appcompat)
    implementation(libs.hilt.android)
}
