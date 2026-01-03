plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    kotlin("plugin.parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.wodox.main"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.wodox.demo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX Core
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
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    kapt(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation("org.jsoup:jsoup:1.18.1")

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)
    // Gson
    implementation(libs.gson)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // EventBus
    implementation(libs.eventbus)
    implementation("com.kizitonwose.calendar:view:2.3.0")
    // Glide
    implementation(libs.glide)
    implementation(libs.okhttp3.integration)
    kapt(libs.compiler)
    implementation(libs.firebase.auth)

    // Firebase
    implementation(platform(libs.firebase.bom))
    // Firebase SDKs
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.database)
    implementation(libs.card.stack.view)
    implementation(libs.androidx.paging.runtime)
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation(libs.number.picker)
    // Project modules
    implementation(project(":app:home"))
    implementation(project(":app:common"))
    implementation(project(":app:setting"))
    implementation(project(":app:intro"))
    implementation(project(":app:docs"))
    implementation(project(":app:chat"))
    implementation(project(":app:auth"))
    implementation(project(":app:core"))
    implementation(project(":app:calendar"))
    implementation(project(":app:mywork"))
    implementation(project(":app:resources"))
    implementation(project(":domain:chat"))
    implementation(project(":domain:mywork"))
    implementation(project(":domain:main"))
    implementation(project(":domain:remote"))
    implementation(project(":domain:asset"))
    implementation(project(":domain:common"))
    implementation(project(":domain:home"))
    implementation(project(":domain:user"))
    implementation(project(":data:home"))
    implementation(project(":data:chat"))
    implementation(project(":data:remote"))
    implementation(project(":data:asset"))
    implementation(project(":data:prefs"))
    implementation(project(":domain:base"))
    implementation(project(":data:user"))
    implementation(project(":domain:user"))
    implementation(project(":data:common"))
    implementation(project(":config"))
    implementation(project(":rteditor"))
}
