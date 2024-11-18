buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.secrets.gradle.plugin)
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    kotlin("kapt") version "1.9.23"
    alias(libs.plugins.compose.compiler) apply false
}