plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    //`maven-publish`
}

android {
    namespace = "com.clovero.mmkv"
    compileSdk = 33

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    api("com.tencent:mmkv:1.3.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                artifactId = "mmkv-ktx"
//                version = "1.0"
//            }
//        }
//    }
//}