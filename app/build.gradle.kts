plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.application.pethome"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.application.pethome"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.firebase.auth.ktx)
    //Fragmentos
    val fragment_version = "1.5.5"

    implementation("androidx.fragment:fragment:$fragment_version")
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    //Splash
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Navegacion
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")

    //Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    //Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")

    //Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")

    //Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    //Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    //Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}