plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pennypin"
    compileSdk = 35
    buildFeatures {
        buildConfig= true
    }

    defaultConfig {
        applicationId = "com.example.pennypin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.room.runtime)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.room.ktx)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.datastore.preferences.core.android)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.play.services.cast.framework)
    kapt(libs.room.compiler)
    implementation ("io.ktor:ktor-client-core:2.3.5")

    // Engine for Android (instead of CIO, use OkHttp for Android)
    implementation ("io.ktor:ktor-client-okhttp:2.3.5")

    // JSON serialization
    implementation ("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.room:room-ktx:2.7.2") // for coroutines support
    implementation(libs.androidx.work.runtime.ktx)
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("com.google.code.gson:gson:2.10.1")
        // ... your other dependencies

        // Add this line for Jetpack DataStore
        implementation ("androidx.datastore:datastore-preferences")

        // You might also want the core dependency
        implementation ("androidx.datastore:datastore-core")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")


}