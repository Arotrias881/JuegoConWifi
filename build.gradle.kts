// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Compose compiler plugin necesario con Kotlin 2.0+ (distinto al kotlin-android que ya viene en AGP 9)
    alias(libs.plugins.kotlin.compose) apply false
}
