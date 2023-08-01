plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    //id("com.diffplug.spotless") version "6.20.0"
}

allprojects {
    group = "com.github.clovero"
}