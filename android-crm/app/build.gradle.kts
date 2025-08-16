    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        id("com.google.gms.google-services")
        id("com.google.devtools.ksp") version "2.0.21-1.0.26"

    }

    android {
        namespace = "gautam.projects.crm_assignment"
        compileSdk = 36

        defaultConfig {
            applicationId = "gautam.projects.crm_assignment"
            minSdk = 29
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        buildFeatures {
            viewBinding = true
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


        implementation(libs.androidx.work.runtime.ktx)
        //viewModels
        val lifecycle_version = "2.8.3"
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
        implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-database")        // Room
        val room_version = "2.6.1"
        implementation("androidx.room:room-runtime:$room_version")
        implementation("androidx.room:room-ktx:$room_version")
        ksp("androidx.room:room-compiler:$room_version")

        //okHttp
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

        //Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // Other standard libraries
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)


    }

