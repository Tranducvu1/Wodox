plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.wodox.mywork"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
// Androidx Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)

    // Material
    implementation(libs.material)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)


    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.paging.runtime.ktx)
    kapt(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Gson
    implementation(libs.gson)

    // Glide
    implementation(libs.glide)
    implementation(libs.okhttp3.integration)
    kapt(libs.hilt.android.compiler)
    //flex box
    implementation (libs.flexbox)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.hilt.navigation.compose)

    // Project modules
    implementation(project(":domain:mywork"))
    implementation(project(":domain:chat"))
    implementation(project(":domain:user"))
    implementation(project(":app:common"))
    implementation(project(":app:resources"))
    implementation(project(":app:core"))
    implementation(project(":domain:remote"))
    implementation(project(":domain:base"))
    implementation(project(":domain:home"))
    implementation(project(":domain:main"))
}