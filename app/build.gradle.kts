

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
//    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"

}

android {
    namespace = "com.example.watch_together"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.watch_together"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "TMDB_API_KEY", "\"${project.findProperty("TMDB_API_KEY") ?: ""}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }



    buildFeatures {
        compose = true
        buildConfig = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.15"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

//dependencies {
//
//    implementation("androidx.core:core-ktx:1.15.0")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
//    implementation("androidx.activity:activity-compose:1.10.1")
//    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
//    implementation("androidx.navigation:navigation-compose:2.8.8")
//    implementation ("androidx.compose.ui:ui-graphics:1.7.8")
//    implementation ("androidx.compose.animation:animation-graphics:1.7.8")
//    implementation("androidx.compose.compiler:compiler:1.5.15")
//    implementation("io.coil-kt:coil-compose:2.1.0")
//
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.2.1")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
//    androidTestImplementation(platform("androidx.compose:compose-bom:2025.02.00"))
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
//
//    implementation ("androidx.core:core-splashscreen:1.0.1")
////    implementation ("com.github.prime-zs.toolkit:core-ktx:2.0.2")
//
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//    implementation ("com.google.dagger:hilt-android:2.50")
//    kapt("com.google.dagger:hilt-android-compiler:2.50")
//
//    implementation("androidx.room:room-runtime:2.6.1")
//    //noinspection KaptUsageInsteadOfKsp
//    kapt("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
//
////    implementation ("com.github.bumptech.glide:glide:4.15.1")
////    implementation ("com.google.accompanist:accompanist-glide:0.28.0")
////    implementation ("com.github.skydoves:landscapist-coil:1.4.0")
//
//
//    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
//    implementation("com.google.firebase:firebase-database")
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.android.gms:play-services-auth:21.3.0")
//
//
//
//
//
//}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0") // Последняя стабильная
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")


    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.8")
    implementation("androidx.compose.animation:animation-graphics:1.7.8")

    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-android-compiler:2.55")

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    implementation("com.google.android.gms:play-services-auth:21.3.0")
}

