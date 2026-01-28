plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.android_data_transfer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.android_data_transfer"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
//navigation
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    // permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    // hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation(libs.androidx.ui.text.google.fonts)
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    //Glide
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")
    // anim
    implementation(libs.lottie.compose)
    // firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.ads)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // project
    val ktor_version = "2.3.12"
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    // ZXing
    implementation("com.google.zxing:core:3.5.3")
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // Code Scanner
    implementation(libs.play.services.code.scanner)
}