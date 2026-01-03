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
    namespace = "com.wodox.home"
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
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.room.room.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.dynamic.links.ktx)
    implementation(libs.androidx.hilt.common)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation("com.google.firebase:firebase-dynamic-links-ktx")

    // Gson
    implementation(libs.gson)

    // Glide
    implementation(libs.glide)
    implementation(libs.okhttp3.integration)

    // Flexbox
    implementation(libs.flexbox)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Compose
    implementation(libs.androidx.activity.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation & UI
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.number.picker)

    // EventBus
    implementation(libs.eventbus)

    // Material Design
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    // Project modules
    implementation(project(":app:core"))
    implementation(project(":app:common"))
    implementation(project(":app:resources"))
    implementation(project(":domain:remote"))
    implementation(project(":domain:home"))
    implementation(project(":domain:docs"))
    implementation(project(":domain:base"))
    implementation(project(":domain:main"))
    implementation(project(":domain:chat"))
    implementation(project(":domain:user"))
    implementation(project(":data:home"))
    implementation(project(":config"))
}